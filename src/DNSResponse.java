public class DNSResponse {

    private String ip;
    private boolean isFinal;
    private int type;


    public DNSResponse(String ip, boolean isFinal, int type){
        this.ip = ip;
        this.isFinal = isFinal;
        this.type = type;
    }

    public DNSResponse(){
        // Default Constructor 
    }

    public void setIp(String ip){
        this.ip = ip;
    }

    public void setIsFinal(boolean isFinal){
        this.isFinal = isFinal;
    }

    public void setType(int type){
        this.type = type;
    }

    public String getIp(){
        return this.ip;
    }

    public boolean getIsFinal(){
        return this.isFinal;
    }

    public int getType(){
        return this.type;
    }
}
