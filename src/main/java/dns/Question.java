package dns;

public class Question {

    private String domain;

    private Type type;

    /**
     * in practice always set to 1
     */
    private RecordClass clazz;

    private int byteSize;

    public Question(String domain, short type, short clazz) {
        this.domain = domain;
        this.type = Type.toType(type);
        this.clazz = RecordClass.toClass(clazz);
        byteSize = 4 + domain.length();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("domain:");
        stringBuilder.append(domain);
        stringBuilder.append("\ntype:");
        stringBuilder.append(type);
        stringBuilder.append("\nclass:");
        stringBuilder.append(clazz);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        if (obj instanceof Question) {
            return domain.equals(((Question) obj).domain) && type == ((Question) obj).type &&
                clazz == ((Question) obj).clazz;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (domain + type.toString() + clazz.toString()).hashCode();
    }

    public String getDomain() { return domain; }

    public Type getType() { return type; }

    public RecordClass getClazz() { return clazz; }

    public int getByteSize() { return byteSize; }

}
