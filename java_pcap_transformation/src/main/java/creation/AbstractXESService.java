package creation;

import packets.PcapPacket;

import java.net.InetAddress;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractXESService {
    protected Logger logger = Logger.getLogger(AbstractXESService.class.getName());

    private static String SERVICE_NAME;
    private InetAddress serviceIP;

    private String teamName;
    private InetAddress teamIP;
    private String teamMask;

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
