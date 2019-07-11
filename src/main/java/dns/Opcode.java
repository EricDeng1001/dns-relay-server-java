package dns;

public enum Opcode {
    Query,
    Other;

    public static Opcode toOpcode(byte opcode) {
        switch (opcode) {
            case 0:
                return Query;
            default:
                return Other;
        }
    }

    public static byte toByte(Opcode opcode) {
        switch (opcode) {
            case Query:
                return 0;
            default:
                return -1;
        }
    }
}

