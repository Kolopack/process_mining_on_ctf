package creation;

import constants.XESConstants;
import exceptions.PacketListIsEmptyException;
import exceptions.UnavailableException;
import org.w3c.dom.Element;
import packets.Mostwanted;
import packets.PcapPacket;
import packets.Session;
import xeshandling.DefaultEventCreator;
import xeshandling.ElementCreator;
import xeshandling.MostwantedReader;
import xeshandling.XESManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MostwantedService extends AbstractXESService implements IService {
    private static final String MOSTWANTED = "Mostwanted";
    private static final String MOSTWANTED_IP_STRING = "10.14.1.9";
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
        XESManager manager = new XESManager(xesPath, MOSTWANTED + "_" + getTeamName());

        try {
            if (isOrderOfPacketsTrue()) {
                logger.info("Packets are in correct order");
            } else {
                throw new UnavailableException();
            }
        } catch (UnavailableException e) {
            e.printStackTrace();
        }

        //List<Session> handshakes = DefaultEventCreator.checkForThreeWayHandshake(packetList);
        //List<List<PcapPacket>> finishes = DefaultEventCreator.checkForFinishing(packetList);

        //System.out.println("Handshakes-count: " + handshakes.size());
        //System.out.println("Finishes-count: " + finishes.size());

        MostwantedReader mostwantedReader=new MostwantedReader();
        //List<Mostwanted> mostwanteds = DefaultEventCreator.getPSHACKSessionsBetween(handshakes, finishes, packetList, getTeamIP(), MOSTWANTED_IP);
        List<Mostwanted> mostwanteds = mostwantedReader.getMostwanteds(packetList,getTeamIP(),MOSTWANTED_IP);

        System.out.println("The following Mostwanteds were detected: (" + mostwanteds.size() + ")");
        int counter = 1;
        Mostwanted first = mostwanteds.get(0);
        System.out.println("Counter: " + counter);
        System.out.println("*");
        System.out.println(first);
        System.out.println("*");

        createMostwantedXES(mostwanteds, manager);
    }

    private void createMostwantedXES(List<Mostwanted> mostwantedList, XESManager xesManager) {
        System.out.println("Nun wird XES gebaut :-)");

        Element serviceName = getServiceNameElement(xesManager);
        xesManager.addNewElementToLog(serviceName);

        Element teamName = getTeamNameElement(xesManager);
        xesManager.addNewElementToLog(teamName);

        for (Mostwanted mostwanted : mostwantedList) {
            Element mostwantedTrace = getTraceForMostwanted(mostwanted, xesManager);
            xesManager.addNewElementToLog(mostwantedTrace);
        }

        xesManager.finishFile();
    }

    private Element getServiceNameElement(XESManager xesManager) {
        HashMap<String, String> serviceParameters = new HashMap<>();
        serviceParameters.put(XESConstants.KEY_STRING, "service");
        serviceParameters.put(XESConstants.VALUE_STRING, MOSTWANTED);
        Element service = xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, serviceParameters);
        return service;
    }

    private Element getTeamNameElement(XESManager xesManager) {
        HashMap<String, String> teamnameParameters = new HashMap<>();
        teamnameParameters.put(XESConstants.KEY_STRING, "teamname");
        teamnameParameters.put(XESConstants.VALUE_STRING, getTeamName());
        Element team = xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, teamnameParameters);
        return team;
    }

    private Element getTraceForMostwanted(Mostwanted mostwanted, XESManager xesManager) {

        //Get Event-Element for Three-Way-Handshake
        List<PcapPacket> handshakes = mostwanted.getThreeWayHandshakePackets();
        Element handshakeEvent = ElementCreator.getHandShakeOrFinishEvent(handshakes, xesManager, XESConstants.HANDSHAKE_CONCEPT_NAME);

        //TODO: here have to be things inbetween: PSHACKs and recognizing HTTP-GET f.i.
        List<PcapPacket> pshAckPackets = mostwanted.getPSHACKAttacks();
        ArrayList<Element> pshAckEvents = ElementCreator.getEventsOfPSHACK(pshAckPackets, xesManager);

        //Get Event-Element for Finishes
        List<PcapPacket> finishes = mostwanted.getTCPFinishingPackets();

        Element finishingEvent = ElementCreator.getHandShakeOrFinishEvent(finishes, xesManager, XESConstants.FINISHING_CONCEPT_NAME);

        ArrayList<Element> traceElements = new ArrayList<>();
        traceElements.add(handshakeEvent);
        for (Element element : pshAckEvents) {
            traceElements.add(element);
        }
        traceElements.add(finishingEvent);

        Element trace = xesManager.createNestedElement(XESConstants.TRACE_ARGUMENT, traceElements);
        return trace;
    }

}
