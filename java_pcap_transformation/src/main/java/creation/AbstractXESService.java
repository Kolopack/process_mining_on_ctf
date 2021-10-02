package creation;

import packets.PcapPacket;

import java.util.List;

public abstract class AbstractXESService {
    private static String SERVICE_NAME;
    private String teamName;
    protected List<PcapPacket> packetList;

    public AbstractXESService(String serviceName, String teamName) {
        SERVICE_NAME=serviceName;
        this.teamName=teamName;
    }
}
