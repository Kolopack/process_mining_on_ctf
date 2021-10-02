package creation;

import packets.PcapPacket;
import xeshandling.XESManager;

import java.util.List;

public class MostwantedService extends AbstractXESService implements IService{

    public MostwantedService(String teamName) {
        super("Mostwanted", teamName);
    }

    @Override
    public void createXESwithList(List<PcapPacket> packetList) {
        this.packetList=packetList;

        XESManager manager=new XESManager();
    }
}
