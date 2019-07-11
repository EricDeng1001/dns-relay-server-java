package dns;

public enum ResponseCode {
    OK,
    NOT_EXIST,
    Other;

    public static ResponseCode toCode(byte code) {
        switch (code) {
            case 0:
                return OK;
            case 3:
                return NOT_EXIST;
            default:
                return Other;
        }
    }

    public static byte toByte(ResponseCode code) {
        switch (code) {
            case OK:
                return 0;
            case NOT_EXIST:
                return 3;
            default:
                return -1;
        }
    }
}

