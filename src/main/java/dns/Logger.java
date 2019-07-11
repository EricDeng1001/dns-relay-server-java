package dns;

import java.util.Date;

public class Logger {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static void info(String x) {
        if (Application.info) {
            System.out.println((new Date().toString()) + "," + x);
        }
    }

    public static void debug(String x) {
        if (Application.debug) {
            System.out.println(x);
        }
    }

    public static void debugBytesToHex(byte[] bytes, int length) {
        if (!Application.debug) {
            return ;
        }
        char[] hexChars = new char[length * 3];
        for (int j = 0; j < length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        System.out.println(new String(hexChars));
    }

    public static String debugBytesToAscii(byte[] bytes, int length) {
        char[] hexChars = new char[length * 3];
        for (int j = 0; j < length; j++) {
            hexChars[j * 3] = ' ';
            hexChars[j * 3 + 1] = (char) bytes[j];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }
}
