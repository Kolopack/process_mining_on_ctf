package creation;

import constants.XESConstants;
import enumerations.Finishes;
import enumerations.Handshakes;
import exceptions.PacketListIsEmptyException;
import exceptions.UnavailableException;
import org.w3c.dom.Element;
import packets.PcapPacket;
import serviceRepresentation.Overcovert;
import xeshandling.*;

import javax.swing.plaf.metal.OceanTheme;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
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

        OvercovertReader overcovertReader=new OvercovertReader();
        List<Overcovert> overcoverts=overcovertReader.getOvercovert(packetList,getTeamIP(),getTeamMask(),OVERCOVERT_IP);

        System.out.println("Found Overcoverts: "+overcoverts.size());
        for(Overcovert overcovert : overcoverts) {
            System.out.println(overcovert+"\n");
        }
        createOvercovertXES(overcoverts,xesManager);
    }

    private void createOvercovertXES(List<Overcovert> overcovertList, XESManager xesManager) {
        System.out.println("Nun wird XES gebaut :-)");

        Element serviceElement=DefaultEventCreator.getServiceNameElement(xesManager,OVERCOVERT);
        xesManager.addNewElementToLog(serviceElement);

        Element teamElement=DefaultEventCreator.getTeamNameElement(xesManager,getTeamName());
        xesManager.addNewElementToLog(teamElement);
    }

    private Element getTraceForOvercovert(Overcovert overcovert, XESManager xesManager) {
        HashMap<Handshakes, PcapPacket> handshakes=overcovert.getHandshakes();

        List<PcapPacket> handshakeList=new ArrayList<PcapPacket>(overcovert.getHandshakes().values());
        Element handshakeElement= ElementCreator.getHandShakeOrFinishEvent(handshakeList,xesManager, XESConstants.HANDSHAKE_CONCEPT_NAME);

        List<PcapPacket> pshackList=overcovert.getInbetween();
        ArrayList<Element> pshackElementList=ElementCreator.getEventsOfPSHACK(pshackList,xesManager);


        List<HashMap<Finishes, PcapPacket>> finishesList=overcovert.getFinishes();
        ArrayList<Element> finishesElements=new ArrayList<>();
        for(HashMap<Finishes, PcapPacket> element : finishesList) {
            List<PcapPacket> temp=new ArrayList<PcapPacket>(element.values());
            Element finishesElement=ElementCreator.getHandShakeOrFinishEvent(temp,xesManager,XESConstants.FINISHING_CONCEPT_NAME);
            finishesElements.add(finishesElement);
        }

    }
}
