import scanning.PcapReader;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    //private static final String directoryPath="src/main/resources/ictf2010.pcap1";
    private static final String pcapFilesDirectory ="src/main/resources/";
    private static final String temporarySystemFolder=System.getProperty("java.io.tmpdir");

    public static void main(String[] args) {
        PcapReader reader=new PcapReader();

        try {
            InetAddress teamIp=InetAddress.getByName("10.13.146.0");
            InetAddress serviceIp=InetAddress.getByName("10.14.1.9");

            reader.importPcap(pcapFilesDirectory, temporarySystemFolder);
            reader.readFiles(teamIp,serviceIp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
