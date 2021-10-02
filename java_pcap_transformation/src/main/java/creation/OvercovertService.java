package creation;

import packets.PcapPacket;

import java.util.List;

public class OvercovertService extends AbstractXESService implements IService{
    public OvercovertService(String teamName) {
        super("Overcovert", teamName);
    }

    @Override
    public void createXESwithList(List<PcapPacket> packetList) {
        this.packetList=packetList;
    }
}
