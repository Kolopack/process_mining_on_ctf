package creation;

import enumerations.MostwantedPart;
import exceptions.PacketListIsEmptyException;
import javafx.util.Pair;
import packets.PcapPacket;
import packets.Session;
import xeshandling.DefaultEventCreator;
import xeshandling.ListManager;
import xeshandling.XESManager;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MostwantedService extends AbstractXESService implements IService {
    private static final String MOSTWANTED = "Mostwanted";

    public MostwantedService(String teamName) {
        super(MOSTWANTED, teamName);
    }

    @Override
    public void createXESwithList(List<PcapPacket> packetList, String xesPath) {
        if (packetList.isEmpty()) {
            throw new PacketListIsEmptyException();
        }

        this.packetList = packetList;
        //XESManager manager=new XESManager(xesPath, MOSTWANTED+"_"+getTeamName());

        if (isOrderOfPacketsTrue()) {
            logger.info("Packets are in correct order");
        }
        List<Session> handshakes = new ArrayList<>();
        List<List<PcapPacket>> finishes=new ArrayList<>();

        /*Long seqA;
        InetAddress client;
        InetAddress server;

        for(int i=0; i<packetList.size();++i) {

            //Current packet
            PcapPacket current=packetList.get(i);
            List<PcapPacket> rest=ListManager.getRestOfList(packetList,i);
            if(current.getTcpFlags().get("SYN")) {
                client=current.getIpSender();
                server=current.getIpReceiver();
                seqA=current.getSeqNumber();

                Pair<Long, PcapPacket> secondPacket= DefaultEventCreator.checkForSecondPacketThreeWayHandshake(rest,server,seqA);
                Long seqB=secondPacket.getKey();

                if(seqB!=null) {


                    Pair<Boolean, PcapPacket> thirdPacket= DefaultEventCreator.checkForThirdPacketThreeWayHandshake(rest,seqB,(seqA+1),client);
                    if(thirdPacket.getKey()) {
                        List<PcapPacket> handshake=new ArrayList();
                        handshake.add(current);
                        handshake.add(secondPacket.getValue());
                        handshake.add(thirdPacket.getValue());

                        handshakes.add(handshake);
                    }
                }

            }
        }*/
        handshakes=DefaultEventCreator.checkForThreeWayHandshake(packetList);
        finishes=DefaultEventCreator.checkForFinishing(packetList);



        System.out.println("The following handshakes were detected: ("+handshakes.size()+")");
        for(Session handshake : handshakes) {
            System.out.println("*");
            System.out.println(handshake);
            System.out.println("*");
        }

        System.out.println("The following finishes were detected: ("+finishes.size()+")");
        for(List<PcapPacket> finish : finishes) {
            System.out.println("*");
            for(PcapPacket packet : finish) {
                System.out.println(packet);
            }
            System.out.println("*");
        }
    }
}
