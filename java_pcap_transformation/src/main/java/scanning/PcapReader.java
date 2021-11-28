package scanning;

import converters.PacketConverter;
import creation.IService;
import creation.ServiceContext;
import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.packet.IPPacket;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.protocol.Protocol;
import packets.PcapPacket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.logging.Logger;

public class PcapReader {
    private Logger logger = Logger.getLogger(PcapReader.class.getName());
    //private static final String pcapEnding=".*\\.pcap.*";
    //private static final String pcapEnding = "ictf2010.pcap31";
    private static final String pcapEnding = "ictf2010.pcap65";

    private File directoryPath;
    private List<File> fileList;
    private List<Storing> storingList;
    private Storing storing;
    private String pathToXES;

    //Team and service setup related variables
    private InetAddress ipService;
    private InetAddress ipTeam;
    private String ipTeamMask;
    private String teamName;
    private String serviceName;

    //Only for statistical printing purposes
    private int packetCounter;
    private int importantPacket;

    public PcapReader(String teamName, String serviceName, String pathToXES) {
        this.teamName=teamName;
        this.serviceName=serviceName;
        this.pathToXES=pathToXES;
        storingList=new ArrayList<>();
    }

    public void importPcap(String pathToDirectory) {
        directoryPath = new File(pathToDirectory);
        importFilepath();
    }

    private void importFilepath() {
        File[] fileArray = directoryPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches(pcapEnding);
            }
        });
        fileList = Arrays.asList(fileArray);
        sortFileListNumerically();
        printOutFileList();
    }

    //Just for testing-purposes
    private void printOutFileList() {
        for (File file : fileList) {
            System.out.println("File-Name: " + file.getName());
        }
    }

    private void sortFileListNumerically() {
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String o1Name=o1.getName().replaceAll("[^0-9]","");
                String o2Name=o2.getName().replaceAll("[^0-9]","");

                int compareO1=Integer.parseInt(o1Name);
                int compareO2=Integer.parseInt(o2Name);

                return  compareO1-compareO2;
            }
        });
    }

    public void readFiles(InetAddress ipTeam, String ipTeamMask, InetAddress ipService) {
        this.ipTeam = ipTeam;
        this.ipTeamMask=ipTeamMask;
        this.ipService = ipService;

        for (File file : fileList) {
            storing=new Storing(teamName,serviceName, file.getName());
            List<PcapPacket> result=filterPCAP(file.getPath(), ipTeam, ipService);
            if(!result.isEmpty()) {
                storingList.add(storing);
            }
            logger.info("File: " + file.getName() + " finished. " + packetCounter + "/" + importantPacket + " total/TCP");
            logger.info("Found "+result.size()+" packets.");
        }
        createXES();
    }

    private List<PcapPacket> filterPCAP(String filePath, InetAddress ipTeam, InetAddress ipService) {
        List<PcapPacket> packets=new ArrayList<>();
        try {

            Pcap pcap = Pcap.openStream(filePath);
            pcap.loop(new PacketHandler() {
                @Override
                public boolean nextPacket(Packet packet) throws IOException {
                    ++packetCounter;

                    if (packet.hasProtocol(Protocol.IPv4)) {
                        IPPacket ipPacket = (IPPacket) packet.getPacket(Protocol.IPv4);

                        InetAddress ipSource = InetAddress.getByName(ipPacket.getSourceIP());
                        InetAddress ipDestination = InetAddress.getByName(ipPacket.getDestinationIP());

                        if (iPisSenderOrReceiver(ipSource, ipDestination)) {
                            System.out.println("packet spotted");
                            if (packet.hasProtocol(Protocol.TCP)) {
                                TCPPacket tcpPacket=(TCPPacket) packet.getPacket(Protocol.TCP);
                                ++importantPacket;
                                if(tcpPacket==null) {
                                    System.out.println("But TCP-Packet is null.");
                                }
                                PcapPacket myPacket=PacketConverter.convertPacket(ipPacket,tcpPacket);
                                packets.add(myPacket);

                            }
                        }
                    }

                    return true;
                }

            });
        } catch (FileNotFoundException e) {
            logger.severe(e.getMessage());
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        if(!packets.isEmpty()){
            storing.storePacketsList(packets);
        }
        return packets;
    }

    private boolean iPisSenderOrReceiver(InetAddress ipSource, InetAddress ipDestination) {
        if (ipSource.equals(ipService) && Network.isInSameNetwork(ipDestination,ipTeam, ipTeamMask)) {
            return true;
        }
        if (Network.isInSameNetwork(ipSource,ipTeam, ipTeamMask) && ipDestination.equals(ipService)) {
            return true;
        }
        return false;
    }



    private void createXES() {
        IService service= ServiceContext.createServiceClass(serviceName, teamName, ipTeam, ipTeamMask);
        service.createXESwithList(getFullServiceList(), pathToXES);
        logger.info("Created all corresponding XES-files.");
    }

    private List<PcapPacket> getFullServiceList() {
        List<PcapPacket> result=new ArrayList<>();

        for(Storing s : storingList) {
            result.addAll(s.readTempPacketsList());
        }
        return result;
    }

    public String getIpTeamMask() {
        return ipTeamMask;
    }
}