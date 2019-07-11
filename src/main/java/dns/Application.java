package dns;

import org.apache.commons.cli.*;

public class Application {

    public static String remoteIp;

    public static boolean info;

    public static boolean debug;

    public static void main(String[] args) {
        Options options = new Options();
        Option domainFilePathString = new Option("c", "config", true, "dns table");
        Option remoteDNSIp = new Option("i", "ip", true, "remote dns ip");
        Option debugLevel1 = new Option("d", false, "set debug level 1");
        Option debugLevel2 = new Option("dd", false, "set debug level 2");

        options.addOption(domainFilePathString);
        options.addOption(remoteDNSIp);
        options.addOption(debugLevel1);
        options.addOption(debugLevel2);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            helpFormatter.printHelp("utility-name", options);
            System.exit(1);
        }

        String domain = cmd.getOptionValue("c", "./dns-table.csv");
        info = cmd.hasOption("d");
        debug = cmd.hasOption("dd") || info;
        remoteIp = cmd.getOptionValue("i", "10.3.9.4");

        Server server = new Server(domain, 53);

        server.run();
    }

}
