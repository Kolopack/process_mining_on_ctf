package creation;

import packets.PcapPacket;
import scanning.PcapReader;

import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractXESService {
    protected Logger logger = Logger.getLogger(AbstractXESService.class.getName());

    private static String SERVICE_NAME;
    private String teamName;
    protected List<PcapPacket> packetList;

    public AbstractXESService(String serviceName, String teamName) {
        SERVICE_NAME=serviceName;
        this.teamName=teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    protected boolean isOrderOfPacketsTrue() {
        boolean result=true;

        PcapPacket before=packetList.get(0);
        for(PcapPacket packet : packetList) {
            if(packet.getArrivalTime().before(before.getArrivalTime())){
                result=false;
            }
        }
        return result;
    }
}
