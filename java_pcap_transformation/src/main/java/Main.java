import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import scanning.PcapReader;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    //private static final String directoryPath="src/main/resources/ictf2010.pcap1";
    private static final String pcapFilesDirectory ="C:/Users/simon/Documents/SS2021/Bachelorarbeit/Daten/pcap";
    private static final String temporarySystemFolder=System.getProperty("java.io.tmpdir");

    private static final String ipTeam="10.13.146.0";
    private static final String ipTeamMask="255.255.255.0";
    private static final String ipService="10.14.1.9";

    public static void main(String[] args) {
        PcapReader reader=new PcapReader();

        try{
            InetAddress teamIp=InetAddress.getByName(ipTeam);
            InetAddress serviceIp=InetAddress.getByName(ipService);

            reader.importPcap(pcapFilesDirectory, temporarySystemFolder);
            reader.readFiles(teamIp,ipTeamMask,serviceIp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }




    }
}
