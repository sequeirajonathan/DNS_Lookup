import java.net.InetAddress;

public class mydns {

    public static void main(String[] args) throws Exception {
        final int MIN_ARGUMENTS = 2;
        final int MAX_ARGUMENTS = 3;
        final int SOCKET = 9876;
        
        int argCount = args.length;
        InetAddress rootNameServer;

        if (argCount < MIN_ARGUMENTS || argCount > MAX_ARGUMENTS) {
            display();
            return;
        }

        DNSClient client = new DNSClient(args[0], args[1], SOCKET);

        rootNameServer = InetAddress.getByName(client.getDnsString());

        client.setDNS(rootNameServer);

        DNSResolver runner = client.RUN(rootNameServer);

        if (runner.getAnswer().getIsFinal()) {
            System.out.println(args[1] + " " + runner.getRecordType(runner.getrData().getTYPE()) + " " + runner);
        } else {
            System.out.println(runner.getRecordType(runner.getrData().getTYPE()) + " " + runner.getrData().getIP());
        }
    }

    private static void display() {
        System.out.println("Usage: java -jar DNSlookup.jar rootDNS name ");
        System.out.println("       rootDNS - the IP address (in dotted form) of the root");
        System.out.println("                 DNS server you are to start your search at");
        System.out.println("       name    - fully qualified domain name to lookup");
    }
    
}
