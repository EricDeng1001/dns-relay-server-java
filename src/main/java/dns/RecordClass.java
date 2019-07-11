package dns;

public enum RecordClass {
    In,
    Other;

    public static short toShort(RecordClass clazz) {
        switch (clazz) {
            case In:
                return 1;
            default:
                return -1;
        }
    }

    public static RecordClass toClass(short clazz) {
        switch (clazz) {
            case 1:
                return In;
            default:
                return Other;
        }
    }
}
