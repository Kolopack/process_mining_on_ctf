package creation;

import packets.PcapPacket;

import java.util.List;

public class OvercovertService extends AbstractXESService implements IService{
    private static final String OVERCOVERT="Overcovert";

    public OvercovertService(String teamName) {
        super(OVERCOVERT, teamName);
    }

    @Override
    public void createXESwithList(List<PcapPacket> packetList, String xesPath) {
        this.packetList=packetList;
    }

}
