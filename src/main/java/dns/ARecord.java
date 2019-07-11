package dns;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ARecord extends RecordHeader {

    private byte[] ip;

    public ARecord(String domain, String ip) throws UnknownHostException {
        super(domain, Type.A, RecordClass.In, (short) 5192, (short) 4);

        // network byte order
        this.ip = InetAddress.getByName(ip).getAddress();
    }

    /**
     * @return bytes in network byte order
     */
    public byte[] getIp() { return ip; }

}
