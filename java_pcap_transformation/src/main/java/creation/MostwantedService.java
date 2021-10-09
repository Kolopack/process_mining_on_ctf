package creation;

import exceptions.PacketListIsEmptyException;
import packets.PcapPacket;
import xeshandling.DefaultEventCreator;
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

        //Three-way-handshakes
        List<List> handshakes=DefaultEventCreator.checkForThreeWayHandshake(packetList);

        System.out.println("There were the following handshakes detected: ("+handshakes.size()+")");
        for(List<PcapPacket> handshake : handshakes) {
            System.out.println("*");
            for(PcapPacket packet : handshake) {
                System.out.println("SEQ: "+packet.getSeqNumber()+" ACK: "+packet.getAckNumber());
            }
            System.out.println("*");
        }

        //Finishing of connection
        List<List> finishes=DefaultEventCreator.checkForFinishing(packetList);

        System.out.println("There were the following finishes detected: ("+finishes.size()+")");
        for(List<PcapPacket> finish : finishes) {
            System.out.println("*");
            for(PcapPacket packet : finish) {
                System.out.println("SEQ: "+packet.getSeqNumber()+" ACK: "+packet.getAckNumber());
            }
            System.out.println("*");
        }
    }
}
