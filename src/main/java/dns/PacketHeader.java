package dns;

public class PacketHeader {

    /**
     * two bytes id number
     */
    @CorrespondTo("id")
    private short id;

    /**
     * true for response false for query
     */
    @CorrespondTo("QR")
    private boolean isResponseOrQuery;

    /**
     * Typically always 0, see RFC1035 for details.
     */
    @CorrespondTo(value = "OPCODE", bitLength = 4)
    private Opcode opcode;

    /**
     * Set to 1 if the responding server is authoritative - that is, it "owns" - the domain queried.
     */
    @CorrespondTo("AA")
    @ResponseField
    private boolean isAuthoritativeAnswer;

    /**
     * Set to 1 if the message length exceeds 512 bytes.
     * Traditionally a hint that the query can be reissued using TCP, for which the length limitation doesn't apply.
     */
    @CorrespondTo("TC")
    @ResponseField
    private boolean isTruncatedMessage;

    @CorrespondTo("RD")
    @QueryField
    private boolean isRecursionDesired;

    @CorrespondTo("RA")
    @ResponseField
    private boolean isRecursionAvailable;

    @CorrespondTo("Z")
    private boolean Z_RESERVED;

    /**
     * Indicates in a response that all data included in the answer and authority
     * sections of the response have been authenticated by the
     * server according to the policies of that server.
     * It should be set only if all data in the response has been cryptographically
     * verified or otherwise meets the server's local security policy.
     */
    @CorrespondTo("AD")
    private boolean isAuthenticatedData;

    @CorrespondTo("CD")
    private boolean isCheckingDisabled;

    @CorrespondTo(value = "RCODE", bitLength = 4)
    @ResponseField
    private ResponseCode responseCode;

    @CorrespondTo("QDCOUNT")
    @QueryField
    private short questionCount;

    @CorrespondTo("ANCOUNT")
    @ResponseField
    private short answerCount;

    @CorrespondTo("NSCOUNT")
    @ResponseField
    private short authorityCount;

    @CorrespondTo("ARCOUNT")
    private short additionalCount;

    public PacketHeader(
        short id, boolean isResponseOrQuery, byte opcode, boolean isAuthoritativeAnswer,
        boolean isTruncatedMessage, boolean isRecursionDesired, boolean isRecursionAvailable,
        boolean z_RESERVED, boolean isAuthenticatedData, boolean isCheckingDisabled, byte responseCode,
        short questionCount, short answerCount,
        short authorityCount, short additionalCount
    ) {
        this.id = id;
        this.isResponseOrQuery = isResponseOrQuery;
        this.opcode = Opcode.toOpcode(opcode);
        this.isAuthoritativeAnswer = isAuthoritativeAnswer;
        this.isTruncatedMessage = isTruncatedMessage;
        this.isRecursionDesired = isRecursionDesired;
        this.isRecursionAvailable = isRecursionAvailable;
        this.Z_RESERVED = z_RESERVED;
        this.isAuthenticatedData = isAuthenticatedData;
        this.isCheckingDisabled = isCheckingDisabled;
        this.responseCode = ResponseCode.toCode(responseCode);
        this.questionCount = questionCount;
        this.answerCount = answerCount;
        this.authorityCount = authorityCount;
        this.additionalCount = additionalCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID:");
        sb.append(id & 0xffff);
        sb.append("\nQR:");
        sb.append(isResponseOrQuery ? "Response": "Query");
        sb.append("\nopcode:");
        sb.append(opcode);
        sb.append("\nAA:");
        sb.append(isAuthoritativeAnswer);
        sb.append("\nTC:");
        sb.append(isTruncatedMessage);
        sb.append("\nRD:");
        sb.append(isRecursionDesired);
        sb.append("\nRA:");
        sb.append(isRecursionAvailable);
        sb.append("\nZ:");
        sb.append(Z_RESERVED);
        sb.append("\nAD:");
        sb.append(isAuthenticatedData);
        sb.append("\nCD:");
        sb.append(isCheckingDisabled);
        sb.append("\nRCODE:");
        sb.append(responseCode);
        sb.append("\n");
        sb.append(questionCount);
        sb.append("\n");
        sb.append(answerCount);
        sb.append("\n");
        sb.append(authorityCount);
        sb.append("\n");
        sb.append(additionalCount);
        return sb.toString();
    }

    public boolean isAuthenticatedData() { return isAuthenticatedData; }

    public boolean isCheckingDisabled() { return isCheckingDisabled; }

    public short getId() { return id; }

    public boolean isResponseOrQuery() { return isResponseOrQuery; }

    public Opcode getOpcode() { return opcode; }

    public boolean isAuthoritativeAnswer() { return isAuthoritativeAnswer; }

    public boolean isTruncatedMessage() { return isTruncatedMessage; }

    public boolean isRecursionDesired() { return isRecursionDesired; }

    public boolean isRecursionAvailable() { return isRecursionAvailable; }

    public boolean isZ_RESERVED() { return Z_RESERVED; }

    public ResponseCode getResponseCode() { return responseCode; }

    public short getQuestionCount() { return questionCount; }

    public short getAnswerCount() { return answerCount; }

    public short getAuthorityCount() { return authorityCount; }

    public short getAdditionalCount() { return additionalCount; }

}
