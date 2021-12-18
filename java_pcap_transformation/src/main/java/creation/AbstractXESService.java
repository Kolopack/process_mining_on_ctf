package creation;

import packets.PcapPacket;

import java.net.InetAddress;
import java.util.List;
import java.util.logging.Logger;

/**
 * Abstract class to define fields and method-logic valid for all Service-types (which are extending this abstract
 * class)
 */
public abstract class AbstractXESService {
    protected Logger logger = Logger.getLogger(AbstractXESService.class.getName());

    /**
     * Name of the service as a String (f.i. Mostwanted), required for creating the filename of the XES-files.
     */
    private static String SERVICE_NAME;
    /**
     * IP of the specific service
     */
    private InetAddress serviceIP;
    /**
     * Name of the team as a String (f.i. Bushwhackers), required for creating the filename of the XES-files.
     */
    private String teamName;
    /**
     * IP of the specific team analysed
     */
    private InetAddress teamIP;
    /**
     * IP(v4) Subnet-mask of the team-network as a String (f.i. "255.255.255.0").
     * For checking and filtering the packets which come from the network of the team.
     */
    private String teamMask;
    /**
     * The packetlist containing all filtered packets which belong to this service
     */
    protected List<PcapPacket> packetList;

    /**
     * Constructor for creating AbstractXESService, and so dealing with all forms of implemented Services, as they are all
     * extending from this abstract class.
     * @param serviceName Name of the service as String
     * @param teamName Name of the team as String
     * @param teamIP IP-address of the team as java.net.InetAddress-object
     * @param teamMask Subnetmask of the team as String (in format f.i. 255.0.0.0)
     * @param serviceIP IP-address of the service as java.net.InetAddress-object
     */
    public AbstractXESService(String serviceName, String teamName, InetAddress teamIP, String teamMask, InetAddress serviceIP) {
        SERVICE_NAME=serviceName;
        this.teamName=teamName;
        this.teamIP=teamIP;
        this.serviceIP=serviceIP;
        this.teamMask=teamMask;
    }

    /**
     * Gets the teamname
     * @return teamname as String
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * Gets the ip-address of the team
     * @return ip-address of the team as java.net.InetAddress
     */
    public InetAddress getTeamIP() {
        return teamIP;
    }

    /**
     * Method for checking if the order of the packets inside of the List<PcapPacket> is correct, based on the
     * arrivaltimes of the contained packets
     * @return boolean whether they are in correct timely order or not
     */
    protected boolean isOrderOfPacketsTrue() {
        boolean result=true;

        PcapPacket before=packetList.get(0);
        for(PcapPacket packet : packetList) {
            if(packet.getArrivalTime().before(before.getArrivalTime())){
                result=false;
            }
            before=packet;
        }
        return result;
    }

    /**
     * Gets the subnet-mask of the team
     * @return the subnet-mask of the team as a string in the format (f.i. 255.255.0.0)
     */
    public String getTeamMask() {
        return teamMask;
    }
}
