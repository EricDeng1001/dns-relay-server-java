package dns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

public class RequestHandler implements Runnable {

    private static final byte[] ZERO_IP = {(byte) 0, (byte) 0, (byte) 0, (byte) 0};

    private final DatagramSocket socket;

    private final DNSClient client;

    private final DatagramPacket packet;

    RequestHandler(DatagramSocket socket, DatagramPacket packet) throws SocketException {
        this.socket = socket;
        client = new DNSClient(socket);
        this.packet = packet;
    }

    @Override
    public void run() {

        PacketHeader packetHeader = PacketHandler.buildHeader(packet.getData());
        Question question = PacketHandler.buildQuestion(packet.getData());

        Logger.info(
            String.format("%d, %s, %s, %s", packetHeader.getId(), packet.getAddress().toString(), question.getDomain(), question.getType())
        );

        Logger.debug("received:");
        Logger.debugBytesToHex(packet.getData(), packet.getLength());

        Logger.debug(packetHeader.toString());
        Logger.debug(question.toString());

        if (packetHeader.getQuestionCount() > 1) {
            Logger.debug("question more than one");
            client.relay(packet, packetHeader, question);
            return;
        }


        if (question.getType() != Type.A || question.getClazz() != RecordClass.In ||
            packetHeader.getOpcode() != Opcode.Query) {
            Logger.debug("not supported");
            client.relay(packet, packetHeader, question);
            return;
        }

        List<ARecord> answers = ARecordRepo.findByDomain(question.getDomain());

        if (answers == null) {
            Logger.debug("not found in local");
            client.relay(packet, packetHeader, question);
            return;
        }

        byte[] response = new byte[512];
        int responseSize;

        if (Arrays.compare(answers.get(0).getIp(), ZERO_IP) == 0) {
            PacketHeader responseHeader = new PacketHeader(
                packetHeader.getId(),
                true,
                Opcode.toByte(packetHeader.getOpcode()),
                true,
                false,
                packetHeader.isRecursionDesired(),
                true,
                false,
                packetHeader.isAuthenticatedData(),
                packetHeader.isCheckingDisabled(),
                ResponseCode.toByte(ResponseCode.NOT_EXIST),
                packetHeader.getQuestionCount(),
                (short) 0,
                (short) 0,
                (short) 0
            );
            responseSize = 12;
            response = PacketHandler.toBytes(responseHeader);
        } else {
            PacketHeader responseHeader = new PacketHeader(
                packetHeader.getId(),
                true,
                Opcode.toByte(packetHeader.getOpcode()),
                true,
                false,
                packetHeader.isRecursionDesired(),
                true,
                false,
                packetHeader.isAuthenticatedData(),
                packetHeader.isCheckingDisabled(),
                ResponseCode.toByte(ResponseCode.OK),
                packetHeader.getQuestionCount(),
                (short) answers.size(),
                (short) 0,
                (short) 0
            );
            int questionSize = question.getByteSize() + 1;
            System.arraycopy(PacketHandler.toBytes(responseHeader), 0, response, 0, 12);
            System.arraycopy(PacketHandler.toBytes(question), 0, response, 12, questionSize);
            responseSize = 12 + questionSize;

            for (ARecord answer : answers) {
                int answerSize = answer.getByteSize();
                System.arraycopy(PacketHandler.toBytes(answer), 0, response, responseSize, answerSize);
                responseSize += answerSize;
            }
        }
        DatagramPacket responsePacket =
            new DatagramPacket(
                response,
                responseSize,
                packet.getAddress(),
                packet.getPort()
            );

        Logger.debug("send to client:");
        Logger.debugBytesToHex(response, responseSize);

        try {
            socket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
