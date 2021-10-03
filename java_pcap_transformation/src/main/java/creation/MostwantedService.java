package creation;

import exceptions.PacketListIsEmptyException;
import packets.PcapPacket;
import xeshandling.XESManager;

import java.util.List;

public class MostwantedService extends AbstractXESService implements IService{
    private static final String MOSTWANTED="Mostwanted";

    public MostwantedService(String teamName) {
        super(MOSTWANTED, teamName);
    }

    @Override
    public void createXESwithList(List<PcapPacket> packetList, String xesPath) {
        if(packetList.isEmpty()) {
            throw new PacketListIsEmptyException();
        }

        this.packetList=packetList;
        //XESManager manager=new XESManager(xesPath, MOSTWANTED+"_"+getTeamName());

        if(isOrderOfPacketsTrue()) {
            logger.info("Packets are in correct order");
        }

    }
}
