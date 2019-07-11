package dns;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DNSClient {

    private static final ConcurrentMap<Question, DatagramPacket> cache = new ConcurrentHashMap<>();

    private final DatagramSocket serverSocket;

    private final DatagramSocket clientSocket;

    public DNSClient(DatagramSocket socket) throws SocketException {
        this.serverSocket = socket;
        clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(10000);
    }

    public void relay(DatagramPacket packet, PacketHeader packetHeader, Question question) {
        Logger.debug("relay packet");
        DatagramPacket cached = cache.get(question);
        byte[] buf = new byte[512];
        InetAddress origin = packet.getAddress();
        int port = packet.getPort();

        if (cached != null) {
            Logger.debug("found cache");
            PacketHeader cachedHeader = PacketHandler.buildHeader(cached.getData());
            PacketHeader header = new PacketHeader(
                packetHeader.getId(),
                true,
                Opcode.toByte(packetHeader.getOpcode()),
                cachedHeader.isAuthoritativeAnswer(),
                false,
                packetHeader.isRecursionDesired(),
                cachedHeader.isAuthoritativeAnswer(),
                cachedHeader.isZ_RESERVED(),
                cachedHeader.isAuthenticatedData(),
                cachedHeader.isCheckingDisabled(),
                ResponseCode.toByte(cachedHeader.getResponseCode()),
                packetHeader.getQuestionCount(),
                cachedHeader.getAnswerCount(),
                cachedHeader.getAuthorityCount(),
                cachedHeader.getAdditionalCount()
            );
            System.arraycopy(PacketHandler.toBytes(header), 0, buf, 0, 12);
            System.arraycopy(cached.getData(), 12, buf, 12, cached.getLength() - 12);
            DatagramPacket response = new DatagramPacket(buf, cached.getLength(), origin, port);
            try {
                serverSocket.send(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        InetSocketAddress address = new InetSocketAddress(Application.remoteIp, 53);
        packet.setSocketAddress(address);

        DatagramPacket clientReceive = new DatagramPacket(buf, buf.length);
        try {
            clientSocket.send(packet);
            try {
                clientSocket.receive(clientReceive);
                clientReceive.setAddress(origin);
                clientReceive.setPort(port);
                Logger.debug("send to client");
                Logger.debugBytesToHex(clientReceive.getData(), clientReceive.getLength());
                serverSocket.send(clientReceive);
                Logger.debug("going to cache");
                cache.put(question, clientReceive);
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
