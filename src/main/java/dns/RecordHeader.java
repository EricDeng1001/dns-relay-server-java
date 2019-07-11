package dns;

public class RecordHeader {

    private String domain;

    private short type;

    /**
     * in practice always set to 1
     */
    private short clazz;

    private int ttl;

    private short length;

    private int byteSize;

    public RecordHeader(String domain, Type type, RecordClass clazz, short ttl, short length) {
        this(domain, Type.toShort(type), RecordClass.toShort(clazz), ttl, length);
    }

    public RecordHeader(String domain, short type, short clazz, int ttl, short length) {
        this.domain = domain;
        this.type = type;
        this.clazz = clazz;
        this.ttl = ttl;
        this.length = length;

        byteSize = 12 + length;
    }

    public String getDomain() { return domain; }

    public Type getType() { return Type.toType(type); }

    public RecordClass getClazz() { return RecordClass.toClass(clazz); }

    public int getTtl() { return ttl; }

    public short getLength() { return length; }

    public int getByteSize() { return byteSize; }

}
