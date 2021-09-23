import scanning.PcapReader;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    //private static final String directoryPath="src/main/resources/ictf2010.pcap1";
    private static final String pcapFilesDirectory ="C:/Users/simon/Documents/SS2021/Bachelorarbeit/Daten/pcap";
    private static final String temporarySystemFolder=System.getProperty("java.io.tmpdir");

    public static void main(String[] args) {
        PcapReader reader=new PcapReader();

        try {
            InetAddress teamIp=InetAddress.getByName("10.13.146.1");
            InetAddress serviceIp=InetAddress.getByName("10.14.1.9");

            reader.importPcap(pcapFilesDirectory, temporarySystemFolder);
            reader.readFiles(teamIp,serviceIp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
