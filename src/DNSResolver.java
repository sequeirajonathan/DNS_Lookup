import java.net.InetAddress;
import java.util.ArrayList;

public class DNSResolver {
    private int UUID;
    private int answerCount = 0;
    private int nsCount = 0;
    private int additionalCount = 0;
    private boolean authoritative = false;
    private RData rData;
    private DNSResponse answer;

    public DNSResolver() {
        return;
    }

    public void setAnswer(DNSResponse answer) {
        this.answer = answer;
    }

    public DNSResponse getAnswer() {
        return answer;
    }

    public void setrData(RData rData) {
        this.rData = rData;
    }

    public RData getrData() {
        return rData;
    }

    public boolean getAuthoritave() {
        return this.authoritative;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public int convertBytesToInt(byte[] arr, int offset) {
        return (arr[offset] & 0xFF) << 8 | (arr[offset + 1] & 0xFF);
    }

    public int convert4BytesToInt(byte[] bytes, int offset) {
        return bytes[offset] << 24 | (bytes[offset + 1] & 0xFF) << 16 | (bytes[offset + 2] & 0xFF) << 8
                | (bytes[offset + 3] & 0xFF);
    }

    public String getRecordType(int ClassInt) {
        String actualType = "";
        switch (ClassInt) {
            case 1: {
                actualType = "A";
                break;
            }
            case 2: {
                actualType = "NS";
                break;
            }
            case 5: {
                actualType = "CN";
                break;
            }
        }
        return actualType;
    }

    public byte[] encodeDNSQuery(String query, DNSClient looker) {
        byte[] encodedQuery = new byte[64];
        try {
            query = query.replace(new String(Character.toChars(0)), "");
            String[] split = query.split("\\.");
            int counter = 12;
            int UUID = (int) (Math.random() * 255);
            encodedQuery[1] = (byte) UUID;
            encodedQuery[2] = 0;
            encodedQuery[3] = 0;
            encodedQuery[5] = 1;
            for (int i = 0; i < split.length; i++) {
                encodedQuery[counter] = (byte) split[i].length();
                counter++;
                for (int j = 0; j < split[i].length(); j++) {
                    String a = split[i];
                    char c = a.charAt(j);
                    try {
                        String hexForAscii = Integer.toHexString((int) c);
                        byte test = Byte.parseByte(hexForAscii, 16);
                        encodedQuery[counter] = test;
                        counter++;
                    } catch (Exception e) {
                        break;
                    }
                }
            }

            encodedQuery[counter + 2] = 1;
            encodedQuery[counter + 4] = 1;

        } catch (Exception e) {
        }
        return encodedQuery;
    }

    public byte[] RDataParse(byte[] arr, int offset) {
        byte[] name = new byte[512];
        int count = 0;

        while (true) {

            if ((arr[offset] & 0xFF) == 0) {
                return name;
            }
            if ((arr[offset] & 0xFF) == 192) {
                offset = arr[offset + 1];
                continue;
            }
            if (arr[offset] > 40) {
                name[count] = arr[offset];
                count++;
                offset++;
                continue;
            } else {
                if (count != 0) {
                    name[count] = 0x2e;
                    count++;
                    offset++;
                    continue;
                }
                offset++;
            }
        }
    }

    public RData Reader(byte[] arr, int offset) {

        RData RData = new RData();
        String ipAddress = "";
        int innerOffset = offset + 12;

        try {
            String nameString = new String(RDataParse(arr, offset), "UTF-8");
            String nsString = new String(RDataParse(arr, offset + 11), "UTF-8");
            RData.setNAME(nameString);
            RData.setNS(nsString);
        } catch (Exception e) {
        }

        RData.setTYPE(convertBytesToInt(arr, offset + 2));
        RData.setClassInt(convertBytesToInt(arr, offset + 4));
        RData.setTTL(convert4BytesToInt(arr, offset + 6));
        RData.setRDLength(convertBytesToInt(arr, offset + 10));

        if (RData.getTYPE() == 1 && RData.getRDLength() == 4) {
            byte[] nextIp = new byte[4];
            nextIp[0] = arr[innerOffset];
            nextIp[1] = arr[innerOffset + 1];
            nextIp[2] = arr[innerOffset + 2];
            nextIp[3] = arr[innerOffset + 3];
            try {
                RData.setIP(InetAddress.getByAddress(nextIp).toString().replaceAll("[/ ]", ""));
            } catch (Exception e) {
            }
        }

        for (int i = 0; i < RData.getRDLength(); i++) {
            int ipBits = arr[innerOffset + i];
            ipAddress += ipBits + ".";
        }

        try {
            RData.setIP(InetAddress.getByName(ipAddress.substring(0, ipAddress.length() - 1)).toString()
                    .replaceAll("[/ ]", ""));
        } catch (Exception e) {
        }

        RData.setOffset(12 + RData.getRDLength());

        return RData;
    }

    private void printRDATA(ArrayList<RData> RDataArrayList, DNSClient client) {
        
        int ans = 0;

        if (answerCount >= 1) {
            for (int i = 0; i < answerCount; i++) {
                if (RDataArrayList.get(i).getNAME() != null && RDataArrayList.get(i).getNS().trim().length() > 4) {
                    ans++;
                }
            }
        }

        int ns = 0;

        for (int i = 0; i < RDataArrayList.size(); i++) {
            if (RDataArrayList.get(i).getNAME() != null && RDataArrayList.get(i).getIP() == null && RDataArrayList.get(i).getNS().trim().length() > 4) {
                ns++;
            }
        }


        int add = 0;

        for (int i = 0; i < RDataArrayList.size(); i++) {
            if (RDataArrayList.get(i).getNAME() != null && RDataArrayList.get(i).getIP() != null) {
                add++;
            }
        }


        System.out.println("----------------------------------------------------------------");
        System.out.println("DNS server to query: " + client.getDnsString().replaceAll("[/]", ""));
        System.out.println("Reply received. Content overview: ");
        System.out.println("\t" + ans + " Answers.");
        System.out.println("\t" + ns + " Intermediate Name Servers.");
        System.out.println("\t" + add + " Additional Information Records.");
        System.out.println("Answers section:");

        if (answerCount >= 1) {
            for (int i = 0; i < answerCount; i++) {
                if (RDataArrayList.get(i).getNAME() != null && RDataArrayList.get(i).getNS().trim().length() > 4) {
                    System.out.println("\tName: " + RDataArrayList.get(i).getNAME().trim() + "  Name Server: "
                            + RDataArrayList.get(i).getIP().trim());
                }
            }
        }

        System.out.println("Authoritive Section:");

        for (int i = 0; i < RDataArrayList.size(); i++) {
            if (RDataArrayList.get(i).getNAME() != null && RDataArrayList.get(i).getIP() == null && RDataArrayList.get(i).getNS().trim().length() > 4) {
                System.out.println("\tName: " + RDataArrayList.get(i).getNAME().trim() + "  Name Server: "
                        + RDataArrayList.get(i).getNS().trim());
            }
        }

        System.out.println("Additional Information Section:");

        for (int i = 0; i < RDataArrayList.size(); i++) {
            if (RDataArrayList.get(i).getNAME() != null && RDataArrayList.get(i).getIP() != null) {
                System.out.println("\tName: " + RDataArrayList.get(i).getNAME().trim() + "  Name Server: "
                        + RDataArrayList.get(i).getIP().trim());
            }
        }

        System.out.println("----------------------------------------------------------------\n");

    }

    public RData decodeDNSQuery(byte[] response, DNSClient client) throws Exception {

        UUID = convertBytesToInt(response, 0);
        answerCount = convertBytesToInt(response, 6);
        nsCount = convertBytesToInt(response, 8);
        additionalCount = convertBytesToInt(response, 10);
        int offset = 12;

        while (response[offset] != 0) {
            offset++;
        }

        int queryType = convertBytesToInt(response, offset + 1);

        if (answerCount >= 1 || queryType == 6) {
            authoritative = true;
        } else {
            authoritative = false;
        }

        offset += 5;

        int totalCount = nsCount + additionalCount + answerCount;

        ArrayList<RData> rDataList = new ArrayList<RData>();

        for (int i = 0; i < totalCount; i++) {
            RData rData = Reader(response, offset);

            if (rData.getTYPE() == 6) {
                authoritative = true;
            }

            offset += rData.getOffset();

            rDataList.add(rData);
        }

        printRDATA(rDataList, client);

        for (int i = 0; i < rDataList.size(); i++) {
            if (answerCount >= 1) {
                return rDataList.get(0);
            } else {
                if (rDataList.get(i).getTYPE() == 1) {
                    return rDataList.get(i);
                }
                if (rDataList.get(i).getTYPE() == 6) {
                    authoritative = true;
                    return rDataList.get(i);
                }
            }
        }

        return rDataList.get(0);
    }
}
