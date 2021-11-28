import packets.PcapPacket;
import scanning.PcapReader;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    private static final String pcapFilesDirectory ="C:/Users/simon/Documents/SS2021/Bachelorarbeit/Daten/pcap";
    private static final String pathToXES="C:/Users/simon/Desktop/";

    //Team Bushwhackers
    private static final String ipBushwhackers ="10.13.146.0";
    private static final String subnetmaskBushwhackers ="255.255.255.0";
    private static final String teamName="Bushwhackers";

    //Service Mostwanted
    private static final String ipMostwanted ="10.14.1.9";
    private static final String mostwantedName ="Mostwanted";

    //Service Overcovert
    private static final String ipOvercovert="10.14.1.10";
    private static final String overcovertName="Overcovert";

    public static void main(String[] args) {
        PcapReader reader=new PcapReader(teamName, mostwantedName, pathToXES);
        PcapReader readerOvercovert=new PcapReader(teamName,overcovertName,pathToXES);

        try{
            InetAddress teamIp=InetAddress.getByName(ipBushwhackers);
            InetAddress mostwantedIp=InetAddress.getByName(ipMostwanted);
            InetAddress overcovertIp=InetAddress.getByName(ipOvercovert);

            /*reader.importPcap(pcapFilesDirectory);
            reader.readFiles(teamIp, subnetmaskBushwhackers,mostwantedIp);
            System.out.println("Finished storing - mostwanted");*/

            readerOvercovert.importPcap(pcapFilesDirectory);
            readerOvercovert.readFiles(teamIp,subnetmaskBushwhackers,overcovertIp);
            System.out.println("Finished storing - overcovert");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static String getTeamName() {
        return teamName;
    }
}
