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

    //Service Flagsubmission
    private static final  String ipFlagsubmission="10.16.13.37";
    private static final  String flagsubmissionName="Flagsubmission";

    public static void main(String[] args) {
        CLI cli=new CLI();

        //PcapReader reader=new PcapReader(teamName, mostwantedName, pathToXES);
        PcapReader reader=new PcapReader(cli.getTeamName(), cli.getServiceName(), cli.getXESFilePath());

        //PcapReader readerOvercovert=new PcapReader(teamName,overcovertName,pathToXES);
        //PcapReader readerFlagsubmission=new PcapReader(teamName,flagsubmissionName,pathToXES);


        try{
            //InetAddress teamIp=InetAddress.getByName(ipBushwhackers);
            InetAddress teamIp=InetAddress.getByName(cli.getIPStringOfTeam());

            //InetAddress mostwantedIp=InetAddress.getByName(ipMostwanted);
            InetAddress mostwantedIp=InetAddress.getByName(cli.getIPStringOfService());

            String subnetmask= cli.getSubnetMaskOfTeam();;

            //InetAddress overcovertIp=InetAddress.getByName(ipOvercovert);
            //InetAddress flagsubmissionIp=InetAddress.getByName(ipFlagsubmission);

            //reader.importPcap(pcapFilesDirectory);
            reader.importPcap(cli.getPCAPFilePathOrDirectory());

            //reader.readFiles(teamIp, subnetmaskBushwhackers,mostwantedIp);
            reader.readFiles(teamIp,subnetmask,mostwantedIp);
            System.out.println("Finished storing - mostwanted");

            /*readerOvercovert.importPcap(pcapFilesDirectory);
            readerOvercovert.readFiles(teamIp,subnetmaskBushwhackers,overcovertIp);
            System.out.println("Finished storing - overcovert");*/

            /*readerFlagsubmission.importPcap(pcapFilesDirectory);
            readerFlagsubmission.readFiles(teamIp,subnetmaskBushwhackers,flagsubmissionIp);
            System.out.println("Finished storing - flagsubmission");*/
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static String getTeamName() {
        return teamName;
    }
}
