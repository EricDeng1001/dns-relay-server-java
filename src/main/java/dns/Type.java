package dns;

enum Type {
    A, AAAA, PTR, CNAME, HINFO, MX, NS, Other;

    public static short toShort(Type type) {
        switch (type) {
            case A:
                return 1;
            case AAAA:
                return 28;
            case PTR:
                return 12;
            case CNAME:
                return 5;
            case HINFO:
                return 13;
            case MX:
                return 15;
            case NS:
                return 2;
            default:
                return -1;
        }
    }

    public static Type toType(short type) {
        switch (type) {
            case 1:
                return A;
            case 28:
                return AAAA;
            case 12:
                return PTR;
            case 5:
                return CNAME;
            case 13:
                return HINFO;
            case 15:
                return MX;
            case 2:
                return NS;
            default:
                return Other;
        }
    }
}
