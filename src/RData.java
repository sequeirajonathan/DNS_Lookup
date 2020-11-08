
public class RData {
    private String IP;
    private String NAME;
    private String NS;
    private int TYPE;
    private int ClassInt;
    private long TTL;
    private int RDLength;
    private int Offset;

    public RData(String IP, String NAME, String NS, int TYPE, int ClassInt, long TTL, int RDLength, int Offset) {
        this.IP = IP;
        this.NAME = NAME;
        this.NS = NS;
        this.TYPE = TYPE;
        this.ClassInt = ClassInt;
        this.TTL = TTL;
        this.RDLength = RDLength;
        this.Offset = Offset;
    }

    public RData() {
        return;
    }

    public void setIP(String iP) {
        IP = iP;
    }

    public void setNAME(String nAME) {
        NAME = nAME;
    }

    public void setNS(String nS) {
        NS = nS;
    }

    public void setTYPE(int tYPE) {
        TYPE = tYPE;
    }

    public void setClassInt(int classInt) {
        ClassInt = classInt;
    }

    public void setTTL(long tTL) {
        TTL = tTL;
    }

    public void setRDLength(int rDLength) {
        RDLength = rDLength;
    }

    public void setOffset(int offset) {
        Offset = offset;
    }

    public String getIP() {
        return IP;
    }

    public String getNAME() {
        return NAME;
    }

    public String getNS() {
        return NS;
    }

    public int getTYPE() {
        return TYPE;
    }

    public int getClassInt() {
        return ClassInt;
    }

    public long getTTL() {
        return TTL;
    }

    public int getRDLength() {
        return RDLength;
    }

    public int getOffset() {
        return Offset;
    }

}
