import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DNSClient {

    private final int TIME_OUT = 5000;
    private final int MAX_QUERIES = 30;
    private DatagramSocket dataSocket;
    private DNSResolver resolve = new DNSResolver();
    private String queryString;
    private String dnsString;
    private int queryCount = 0;
    private InetAddress DNS;

    public DNSClient(String queryString, String dnsString, int socket) {
        try {
            this.queryString = queryString;
            this.dnsString = dnsString;
            this.dataSocket = new DatagramSocket(socket);
            this.resolve = new DNSResolver();
            this.dataSocket.setSoTimeout(TIME_OUT);
        } catch (Exception e) {

        }
    }

    public String getDnsString() {
        return dnsString;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setDNS(InetAddress DNS) {
        this.DNS = DNS;
    }

    private RData DNSLookup(String url, InetAddress rootNameServer) throws Exception {
        byte[] receiveData = new byte[1024];
        byte[] sendData = resolve.encodeDNSQuery(url, this);
        this.dnsString = rootNameServer.toString();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, rootNameServer, 53);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            this.dataSocket.send(sendPacket);
            this.dataSocket.receive(receivePacket);
        } catch (Exception e) {
            System.out.println(this.queryString + " -2 " + "A" + " 0.0.0.0");
        }
        byte[] receiveBytes = receivePacket.getData();
        return this.resolve.decodeDNSQuery(receiveBytes, this);
    }

    public DNSResolver RUN(InetAddress rootNameServer) throws Exception {
        RData nextRes = this.DNSLookup(this.queryString, rootNameServer);
        boolean flag = true;

        while (flag) {
            if (this.resolve.getAuthoritave() & this.resolve.getAnswerCount() == 0) {
                System.out.println(this.queryString + " A -6 0.0.0.0");
                break;
            }
            if (this.resolve.getAnswerCount() > 0) {
                if (nextRes.getTYPE() == 1) {
                    flag = false;
                } else if (nextRes.getNS().length() > 0) {
                    this.queryString = nextRes.getNS();
                    nextRes = this.DNSLookup(this.queryString, this.DNS);
                    this.queryCount++;
                }
            } else {
                if (nextRes != null) {
                    if (nextRes.getIP() == null && nextRes.getNS().length() > 0) {
                        int SEED = (int) (Math.random() * (10000 - 8000)) + 8000;
                        DNSClient temp = new DNSClient(nextRes.getNS(), this.DNS.toString(), SEED);
                        DNSResolver here = temp.RUN(rootNameServer);
                        nextRes = this.DNSLookup(this.queryString, InetAddress.getByName(here.getrData().getIP()));
                    } else {
                        nextRes = this.DNSLookup(this.queryString, InetAddress.getByName(nextRes.getIP()));
                    }
                    queryCount++;
                }
            }

            if (queryCount > MAX_QUERIES) {
                System.out.println(this.queryString + " -3 0.0.0.0");
                flag = false;
            }
        }

        DNSResponse answer = new DNSResponse();
        answer.setIp(nextRes.getIP());
        answer.setType(nextRes.getTYPE());
        if (answer.getType() == 1){
            answer.setIsFinal(true);
        }
        this.dataSocket.close();

        DNSResolver resolved = new DNSResolver();
        resolved.setrData(nextRes);
        resolved.setAnswer(answer);
        return resolved;
    }

}
