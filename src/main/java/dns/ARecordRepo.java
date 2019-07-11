package dns;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ARecordRepo {
    private static Map<String, List<ARecord>> recordMap = new HashMap<>();

    public static List<ARecord> findByDomain(String domain) {
        return recordMap.get(domain);
    }

    public static void readCSV(Path path) throws IOException {
        File file = new File(path.toString());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) {
            String domain = st.substring(0, st.indexOf(','));

            if (domain.charAt(domain.length() - 1) != '.') {
                domain += '.';
            }

            String ip = st.substring(st.indexOf(',') + 1);
            ARecord aRecord = new ARecord(domain, ip);
            List<ARecord> aRecordList = recordMap.get(domain);
            if (aRecordList == null) {
                aRecordList = new LinkedList<>();
            }
            aRecordList.add(aRecord);
            recordMap.put(domain, aRecordList);
        }
    }
}
