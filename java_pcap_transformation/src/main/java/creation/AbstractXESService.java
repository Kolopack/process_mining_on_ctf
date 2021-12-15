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

    public AbstractXESService(String serviceName, String teamName, InetAddress teamIP, String teamMask, InetAddress serviceIP) {
        SERVICE_NAME=serviceName;
        this.teamName=teamName;
        this.teamIP=teamIP;
        this.serviceIP=serviceIP;
        this.teamMask=teamMask;
    }

    public String getTeamName() {
        return teamName;
    }

    public InetAddress getTeamIP() {
        return teamIP;
    }

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

    public String getTeamMask() {
        return teamMask;
    }
}
