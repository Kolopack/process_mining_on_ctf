package creation;

import exceptions.PacketListIsEmptyException;
import exceptions.UnavailableException;
import packets.PcapPacket;
import packets.Session;
import serviceRepresentation.Overcovert;
import xeshandling.DefaultEventCreator;
import xeshandling.OvercovertEventCreator;
import xeshandling.OvercovertReader;
import xeshandling.XESManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class OvercovertService extends AbstractXESService implements IService{
    private static final String OVERCOVERT="Overcovert";
    private static final String OVERCOVERT_IP_STRING="10.14.1.10";
    private static InetAddress OVERCOVERT_IP;

    static {
        try {
            OVERCOVERT_IP = InetAddress.getByName(OVERCOVERT_IP_STRING);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public OvercovertService(String teamName, InetAddress teamIP, String teamMask) {
        super(OVERCOVERT, teamName, teamIP, teamMask,OVERCOVERT_IP);
    }

    @Override
    public void createXESwithList(List<PcapPacket> packetList, String xesPath) {
        if(packetList.isEmpty()) {
            throw new PacketListIsEmptyException();
        }

        this.packetList=packetList;

        try {
            if (isOrderOfPacketsTrue()) {
                logger.info("Packets are in correct order");
            } else {
                throw new UnavailableException();
            }
        } catch (UnavailableException e) {
            e.printStackTrace();
        }
        XESManager xesManager=new XESManager(xesPath, OVERCOVERT+"_"+getTeamName());

        //List<Session> handshakes= DefaultEventCreator.checkForThreeWayHandshake(packetList);
        //List<PcapPacket> resets= OvercovertEventCreator.checkForConnectionResets(packetList);
        //List<PcapPacket> inbeetween;

        OvercovertReader overcovertReader=new OvercovertReader();
        List<Overcovert> overcoverts=overcovertReader.getOvercovert(packetList,getTeamIP(),getTeamMask(),OVERCOVERT_IP);

        System.out.println("Found Overcoverts: "+overcoverts.size());
        for(Overcovert overcovert : overcoverts) {
            System.out.println(overcovert+"\n");
        }
    }

}
