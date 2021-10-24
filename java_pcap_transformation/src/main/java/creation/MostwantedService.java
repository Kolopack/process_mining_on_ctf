package creation;

import exceptions.PacketListIsEmptyException;
import org.w3c.dom.Element;
import packets.Mostwanted;
import packets.PcapPacket;
import packets.Session;
import xeshandling.DefaultEventCreator;
import xeshandling.ElementCreator;
import xeshandling.XESManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MostwantedService extends AbstractXESService implements IService {
    private static final String MOSTWANTED = "Mostwanted";
    private static final String MOSTWANTED_IP_STRING="10.14.1.9";
    private static InetAddress MOSTWANTED_IP;

    static {
        try {
            MOSTWANTED_IP = InetAddress.getByName(MOSTWANTED_IP_STRING);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public MostwantedService(String teamName, InetAddress teamIP) {
        super(MOSTWANTED, teamName, teamIP, MOSTWANTED_IP);
    }

    @Override
    public void createXESwithList(List<PcapPacket> packetList, String xesPath) {
        if (packetList.isEmpty()) {
            throw new PacketListIsEmptyException();
        }

        this.packetList = packetList;
        XESManager manager=new XESManager(xesPath, MOSTWANTED+"_"+getTeamName());

        if (isOrderOfPacketsTrue()) {
            logger.info("Packets are in correct order");
        }
        List<Session> handshakes = new ArrayList<>();
        List<List<PcapPacket>> finishes=new ArrayList<>();

        handshakes=DefaultEventCreator.checkForThreeWayHandshake(packetList);
        finishes=DefaultEventCreator.checkForFinishing(packetList);

        List<Mostwanted> mostwanteds=DefaultEventCreator.getPSHACKSessionsBetween(handshakes,finishes,packetList,getTeamIP(),MOSTWANTED_IP);

        System.out.println("Handshakes-count: "+handshakes.size());
        System.out.println("Finishes-count: "+finishes.size());

        /*System.out.println("The following Mostwanteds were detected: ("+mostwanteds.size()+")");
        int counter=1;
        for(Mostwanted mostwanted : mostwanteds) {
            System.out.println("Counter: "+counter);
            System.out.println("*");
            System.out.println(mostwanted);
            System.out.println("*");
            ++counter;
        }*/
        createMostwantedXES(mostwanteds, manager);
    }

    private void createMostwantedXES(List<Mostwanted> mostwantedList, XESManager xesManager) {
        System.out.println("Nun wird XES gebaut :-)");

        HashMap<String, String> serviceParameters=new HashMap<>();
        serviceParameters.put("key", "service");
        serviceParameters.put("value", MOSTWANTED);
        Element service= xesManager.createSimpleElement("string", serviceParameters);

        xesManager.addNewElementToLog(service);

        System.out.println("Service-Name sollte hinzugefügt worden sein");
    }

    /*private Element getTeamnameParameter() {
        HashMap<String, String> teamnameParameters=new HashMap<>();
        teamnameParameters.put("key","teamname");

    }*/
}
