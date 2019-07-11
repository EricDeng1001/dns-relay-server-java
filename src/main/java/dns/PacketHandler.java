package dns;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketHandler {

    private static final byte QRMask = (byte) 0x80;

    private static final byte OPCODEMask = (byte) 0x78;

    private static final byte AAMask = (byte) 0x04;

    private static final byte TCMask = (byte) 0x02;

    private static final byte RDMask = (byte) 0x01;

    private static final byte RAMask = (byte) 0x80;

    private static final byte ZMask = (byte) 0x40;

    private static final byte ADMask = (byte) 0x20;

    private static final byte CDMask = (byte) 0x10;

    private static final byte RCODEMask = (byte) 0x0F;

    private static final ByteBuffer BYTE_BUFFER = ByteBuffer.allocate(4);

    public static PacketHeader buildHeader(byte[] bytes) {
        BYTE_BUFFER.order(ByteOrder.BIG_ENDIAN);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.put(bytes[0]);
        BYTE_BUFFER.put(bytes[1]);

        short id = BYTE_BUFFER.getShort(0);

        boolean QR = (bytes[2] & QRMask) != 0;
        byte opcode = (byte) (bytes[2] & OPCODEMask);
        opcode >>= 3;

        boolean AA = (bytes[2] & AAMask) != 0;
        boolean TC = (bytes[2] & TCMask) != 0;
        boolean RD = (bytes[2] & RDMask) != 0;
        boolean RA = (bytes[3] & RAMask) != 0;
        boolean Z = (bytes[3] & ZMask) != 0;
        boolean AD = (bytes[3] & ADMask) != 0;
        boolean CD = (bytes[3] & CDMask) != 0;

        byte RCODE = (byte) (bytes[3] & RCODEMask);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.put(bytes[4]);
        BYTE_BUFFER.put(bytes[5]);

        short QDCOUNT = BYTE_BUFFER.getShort(0);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.put(bytes[6]);
        BYTE_BUFFER.put(bytes[7]);

        short ANCOUNT = BYTE_BUFFER.getShort(0);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.put(bytes[8]);
        BYTE_BUFFER.put(bytes[9]);

        short NSCOUNT = BYTE_BUFFER.getShort(0);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.put(bytes[10]);
        BYTE_BUFFER.put(bytes[11]);

        short ARCOUNT = BYTE_BUFFER.getShort(0);

        return new PacketHeader(
            id,
            QR,
            opcode,
            AA,
            TC,
            RD,
            RA,
            Z,
            AD,
            CD,
            RCODE,
            QDCOUNT,
            ANCOUNT,
            NSCOUNT,
            ARCOUNT
        );
    }

    public static Question buildQuestion(byte[] bytes) {
        BYTE_BUFFER.order(ByteOrder.BIG_ENDIAN);

        StringBuilder domain = new StringBuilder();

        int i = 12;
        do {
            int length = bytes[i++] & 0xff;
            while (length != 0) {
                domain.append((char) bytes[i]);
                i++;
                length--;
            }
            domain.append('.');
        } while (bytes[i] != 0);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.put(bytes[++i]);
        BYTE_BUFFER.put(bytes[++i]);

        short QTYPE = BYTE_BUFFER.getShort(0);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.put(bytes[++i]);
        BYTE_BUFFER.put(bytes[++i]);

        short QCLASS = BYTE_BUFFER.getShort(0);

        return new Question(domain.toString(), QTYPE, QCLASS);
    }

    public static byte[] toBytes(Question question) {
        byte[] result = new byte[question.getByteSize() + 1];
        String domain = question.getDomain();

        int i = convertDomainToLabelByte(result, domain);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.putShort(Type.toShort(question.getType()));
        result[i++] = BYTE_BUFFER.get(0);
        result[i++] = BYTE_BUFFER.get(1);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.putShort(RecordClass.toShort(question.getClazz()));
        result[i++] = BYTE_BUFFER.get(0);
        result[i] = BYTE_BUFFER.get(1);

        return result;
    }

    public static byte[] toBytes(PacketHeader packetHeader) {
        byte[] result = new byte[12];

        BYTE_BUFFER.order(ByteOrder.BIG_ENDIAN);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.putShort(packetHeader.getId());

        result[0] = BYTE_BUFFER.get(0);
        result[1] = BYTE_BUFFER.get(1);
        if (packetHeader.isResponseOrQuery()) {
            result[2] |= QRMask;
        }
        if (packetHeader.isAuthoritativeAnswer()) {
            result[2] |= AAMask;
        }
        if (packetHeader.isTruncatedMessage()) {
            result[2] |= TCMask;
        }
        if (packetHeader.isRecursionDesired()) {
            result[2] |= RDMask;
        }
        result[2] |= (byte) (Opcode.toByte(packetHeader.getOpcode()) << 3);

        if (packetHeader.isRecursionAvailable()) {
            result[3] |= RAMask;
        }

        if (packetHeader.isZ_RESERVED()) {
            result[3] |= ZMask;
        }

        if (packetHeader.isAuthenticatedData()) {
            result[3] |= ADMask;
        }

        if (packetHeader.isCheckingDisabled()) {
            result[3] |= CDMask;
        }

        result[3] |= ResponseCode.toByte(packetHeader.getResponseCode());

        BYTE_BUFFER.clear();
        BYTE_BUFFER.putShort(packetHeader.getQuestionCount());
        result[4] = BYTE_BUFFER.get(0);
        result[5] = BYTE_BUFFER.get(1);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.putShort(packetHeader.getAnswerCount());
        result[6] = BYTE_BUFFER.get(0);
        result[7] = BYTE_BUFFER.get(1);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.putShort(packetHeader.getAuthorityCount());
        result[8] = BYTE_BUFFER.get(0);
        result[9] = BYTE_BUFFER.get(1);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.putShort(packetHeader.getAdditionalCount());
        result[10] = BYTE_BUFFER.get(0);
        result[11] = BYTE_BUFFER.get(1);

        return result;
    }

    public static byte[] toBytes(ARecord record) {
        byte[] result = new byte[record.getByteSize()];

        result[0] = (byte)0xC0;
        result[1] = (byte)0x0C;

        BYTE_BUFFER.clear();
        BYTE_BUFFER.putShort(Type.toShort(record.getType()));
        result[2] = BYTE_BUFFER.get(0);
        result[3] = BYTE_BUFFER.get(1);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.putShort(RecordClass.toShort(record.getClazz()));
        result[4] = BYTE_BUFFER.get(0);
        result[5] = BYTE_BUFFER.get(1);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.putInt(record.getTtl());
        result[6] = BYTE_BUFFER.get(0);
        result[7] = BYTE_BUFFER.get(1);
        result[8] = BYTE_BUFFER.get(2);
        result[9] = BYTE_BUFFER.get(3);

        BYTE_BUFFER.clear();
        BYTE_BUFFER.putShort(record.getLength());
        result[10] = BYTE_BUFFER.get(0);
        result[11] = BYTE_BUFFER.get(1);

        byte[] ip = record.getIp();
        result[12] = ip[3];
        result[13] = ip[2];
        result[14] = ip[1];
        result[15] = ip[0];

        return result;
    }

    private static int convertDomainToLabelByte(byte[] result, String domain) {
        int i = 0;
        int j = 0;
        while (i < domain.length()) {
            int length = domain.indexOf('.', i) - i;
            result[j++] = (byte) (length & 0xff);
            while (length != 0) {
                result[j++] = (byte) domain.charAt(i++);
                length--;
            }
            i++;
        }

        result[j++] = 0;
        return j;
    }

}
