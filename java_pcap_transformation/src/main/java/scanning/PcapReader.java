package scanning;

import converters.PacketConverter;
import creation.IService;
import creation.ServiceContext;
import io.pkts.Pcap;
import io.pkts.packet.IPPacket;
import io.pkts.packet.TCPPacket;
import io.pkts.protocol.Protocol;
import packets.PcapPacket;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * The type Pcap reader.
 * In this class, the io.pkts Package is used to read the different packets out of the PCAP-files.
 * They are immediately filtered using this PCAP-library, then the results are handed over to the Converter-class
 * to convert them to our internal packet-objects. After that, they are processed for starting the transformation-
 * process.
 */
public class PcapReader {
    /**
     * Logger-instance for enabling Logging, which is important in this class
     */
    private final Logger logger = Logger.getLogger(PcapReader.class.getName());
    /**
     * The ending. With this Regular Expression, all PCAP-files are reached
     */
    private static final String pcapEnding=".*\\.pcap.*";
    /**
     * File-object which holds the path to the directory which is to be read for PCAP-files
     */
    private File directoryPath;
    /**
     * List of File-elements (will be filled with found PCAP-files)
     */
    private List<File> fileList;
    /**
     * List of Storing-objects, one object for each found PCAP-file (to create the corresponding temp-files
     * for storing the found packets temporarily)
     */
    private final List<Storing> storingList;
    /**
     * Instance of Storing, for storing the current Storing-object
     */
    private Storing storing;
    /**
     * The path to the XES to be created as String
     */
    private final String pathToXES;

    /**
     * TEAM AND SERVICE SETUP RELATED VARIABLES
     *
     * IP-address of the Service, as java.net.InetAddress
     */
    private InetAddress ipService;
    /**
     * IP-address of the Team, as java-net.InetAddress
     */
    private InetAddress ipTeam;
    /**
     * Subnet-mask of the team as String in format (f.i. 255.255.0.0)
     */
    private String ipTeamMask;
    /**
     * Name of the team as String
     */
    private final String teamName;
    /**
     * Name of the Service as String
     */
    private final String serviceName;

    /**
     * ONLY FOR STATISTICAL PURPOSES AND USER-INFORMATION:
     *
     * Counter which holds the amount of packets found
     */
    private int packetCounter;
    /**
     * Counter which holds the amount of packets considered important for our filter
     */
    private int importantPacket;

    /**
     * Instantiates a new Pcap reader.
     *
     * @param teamName    the team name
     * @param serviceName the service name
     * @param pathToXES   the path to xes
     */
    public PcapReader(String teamName, String serviceName, String pathToXES) {
        this.teamName=teamName;
        this.serviceName=serviceName;
        this.pathToXES=pathToXES;
        storingList=new ArrayList<>();
    }

    /**
     * Import pcap.
     *
     * @param pathToDirectory the path to directory
     */
    public void importPcap(String pathToDirectory) {
        directoryPath = new File(pathToDirectory);
        importFilepath();
    }

    /**
     * Method to import the Filepath. The path is read and an Array of files is received containing
     * the PCAP-files found at this path.
     */
    private void importFilepath() {
        File[] fileArray = directoryPath.listFiles((dir, name) -> name.matches(pcapEnding));
        if(fileArray!=null) {
            fileList = Arrays.asList(fileArray);
        }
        sortFileListNumerically();
        printOutFileList();
    }

    /**
     * Method for printing out the File-list, just for testing-purposes and user-information
     */
    private void printOutFileList() {
        for (File file : fileList) {
            System.out.println("File-Name: " + file.getName());
        }
    }

    /**
     * Method which sorts the list of files based on their naming numerically.
     * Parts of this method are taken from stackoverflow.
     * CITE: https://stackoverflow.com/questions/4623446/how-do-you-sort-files-numerically
     */
    private void sortFileListNumerically() {
        fileList.sort((o1, o2) -> {
            String o1Name = o1.getName().replaceAll("[^0-9]", "");
            String o2Name = o2.getName().replaceAll("[^0-9]", "");

            int compareO1 = Integer.parseInt(o1Name);
            int compareO2 = Integer.parseInt(o2Name);

            return compareO1 - compareO2;
        });
    }

    /**
     * Read files.
     *
     * @param ipTeam     the ip team
     * @param ipTeamMask the ip team mask
     * @param ipService  the ip service
     */
    public void readFiles(InetAddress ipTeam, String ipTeamMask, InetAddress ipService) {
        this.ipTeam = ipTeam;
        this.ipTeamMask=ipTeamMask;
        this.ipService = ipService;

        for (File file : fileList) {
            storing=new Storing(teamName,serviceName, file.getName());
            List<PcapPacket> result=filterPCAP(file.getPath());
            if(!result.isEmpty()) {
                storingList.add(storing);
            }
            logger.info("File: " + file.getName() + " finished. " + packetCounter + "/" + importantPacket + " total/TCP");
            logger.info("Found "+result.size()+" packets.");
            packetCounter=0;
            importantPacket=0;
        }
        createXES();
    }

    /**
     * Method which applies the io.pkts.Pcap-library to read and filter the packets based on the input by the
     * user. The filtered packets are handed over for converting and further processing
     * @param filePath The path to the file to be read as a String
     * @return List of PcapPackets found in this file, which are results of the applied filters
     */
    private List<PcapPacket> filterPCAP(String filePath) {
        List<PcapPacket> packets=new ArrayList<>();
        try {

            Pcap pcap = Pcap.openStream(filePath);
            pcap.loop(packet -> {
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
                            System.out.println("One of them, here are the packets:");
                            System.out.println("IP-packet: ");
                            System.out.println(ipPacket.getPayload());
                            System.out.println("TCP-Packet: ");
                            if (tcpPacket != null) {
                                System.out.println("TCP-Payload: " + tcpPacket.getPayload());
                            } else {
                                System.out.println("null");
                            }
                            PcapPacket myPacket=PacketConverter.convertPacket(ipPacket,tcpPacket);
                            packets.add(myPacket);

                        }
                    }
                }

                return true;
            });
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        if(!packets.isEmpty()){
            storing.storePacketsList(packets);
        }
        return packets;
    }

    /**
     * Method for checking whether the given IP-addresses match the IPs of Service and Team
     * (For filtering the packets and receiving only packets relevant for us).
     * @param ipSource IP-address of the source of a packet in java.net.InetAddress
     * @param ipDestination IP-address of the destination of a packet in java.net.InetAddress
     * @return true or false if the IPs match with the service and team (or team and service) IPs
     */
    private boolean iPisSenderOrReceiver(InetAddress ipSource, InetAddress ipDestination) {
        if (ipSource.equals(ipService) && Network.isInSameNetwork(ipDestination,ipTeam, ipTeamMask)) {
            return true;
        }
        return Network.isInSameNetwork(ipSource, ipTeam, ipTeamMask) && ipDestination.equals(ipService);
    }

    /**
     * Method for creating the XES-file with given fields as parameters
     */
    private void createXES() {
        IService service= ServiceContext.createServiceClass(serviceName, teamName, ipTeam, ipTeamMask);
        service.createXESwithList(getFullServiceList(), pathToXES);
        logger.info("Created all corresponding XES-files.");
    }

    /**
     * Method which merges the Lists of PcapPackets found in different PCAP-files together to one List,
     * which then contains the full amount of filtered packets of all files
     * @return List<PcapPacket> containing all packets which fulfill the filters
     */
    private List<PcapPacket> getFullServiceList() {
        List<PcapPacket> result=new ArrayList<>();

        for(Storing s : storingList) {
            result.addAll(s.readTempPacketsList());
        }
        return result;
    }
}