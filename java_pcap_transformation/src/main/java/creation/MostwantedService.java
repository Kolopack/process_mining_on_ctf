package creation;

import constants.XESConstants;
import exceptions.PacketListIsEmptyException;
import exceptions.UnavailableException;
import org.w3c.dom.Element;
import packets.PcapPacket;
import packets.Session;
import serviceRepresentation.Mostwanted;
import xeshandling.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service-class containing the methods and logic for creating the XES for the Mostwanted-service
 */
public class MostwantedService extends AbstractXESService implements IService {
    /**
     * Name of the Mostwanted-service as string, for setting the file-name of XES
     */
    private static final String MOSTWANTED = "Mostwanted";
    /**
     * The IP-address of the Service, for setting up the InetAddress-Object
     */
    private static final String MOSTWANTED_IP_STRING = "10.14.1.9";
    /**
     * The IP-address of the Service as java.net.InetAddress-field
     */
    private static InetAddress MOSTWANTED_IP;

    static {
        try {
            MOSTWANTED_IP = InetAddress.getByName(MOSTWANTED_IP_STRING);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for the MostwantedService, as indicated in the abstract class (AbstractXESService)
     * @param teamName Name of the team as String
     * @param teamIP IP-address of the team as java.net.InetAddress
     * @param teamMask Subnet-mask of the team-network as a String in format (f.i. 255.255.0.0)
     */
    public MostwantedService(String teamName, InetAddress teamIP, String teamMask) {
        super(MOSTWANTED, teamName, teamIP, teamMask, MOSTWANTED_IP);
    }

    /**
     * Overridden method of the implemented interface, for creating a XES-file with given List of PcapPackets
     * @param packetList List<PcapPacket> containing the PcapPackets
     * @param xesPath The path to the XES-file to be created as a String
     */
    @Override
    public void createXESwithList(List<PcapPacket> packetList, String xesPath) {
        if (packetList.isEmpty()) {
            throw new PacketListIsEmptyException();
        }
        this.packetList = packetList;

        try {
            if (isOrderOfPacketsTrue()) {
                logger.info("Packets are in correct order");
            } else {
                throw new UnavailableException();
            }
        } catch (UnavailableException e) {
            e.printStackTrace();
        }

        XESManager manager = new XESManager(xesPath, MOSTWANTED + "_" + getTeamName());

        List<Session> handshakes = DefaultEventCreator.checkForThreeWayHandshake(packetList);
        List<List<PcapPacket>> finishes = DefaultEventCreator.checkForFinishing(packetList);

        System.out.println("Handshakes-count: " + handshakes.size());
        System.out.println("Finishes-count: " + finishes.size());

        List<Mostwanted> mostwanteds = MostwantedEventCreator.getPSHACKSessionsBetween(handshakes, finishes, packetList, getTeamIP(), MOSTWANTED_IP);

        System.out.println("The following Mostwanteds were detected: (" + mostwanteds.size() + ")");
        createMostwantedXES(mostwanteds, manager);
    }

    /**
     * Creates the XES setting up all the Mostwanted-traces and tag-attributes
     * @param mostwantedList List of Mostwanted-objects found
     * @param xesManager Instance of XESManager, to create Java DOM-elements
     */
    private void createMostwantedXES(List<Mostwanted> mostwantedList, XESManager xesManager) {
        System.out.println("Nun wird XES gebaut :-)");

        Element serviceName = DefaultEventCreator.getServiceNameElement(xesManager,MOSTWANTED);
        xesManager.addNewElementToLog(serviceName);

        Element teamName = DefaultEventCreator.getTeamNameElement(xesManager,getTeamName());
        xesManager.addNewElementToLog(teamName);

        for (Mostwanted mostwanted : mostwantedList) {
            Element mostwantedTrace = getTraceForMostwanted(mostwanted, xesManager);
            xesManager.addNewElementToLog(mostwantedTrace);
        }

        xesManager.finishFile();
    }

    /**
     * Sets up and delivers the Trace-DOM-object for a given Mostwanted-object
     * @param mostwanted Object of Mostwanted
     * @param xesManager Instance of XESManager
     * @return Trace for one Mostwanted, as Java-DOM-Element
     */
    private Element getTraceForMostwanted(Mostwanted mostwanted, XESManager xesManager) {

        //Get Event-Element for Three-Way-Handshake
        List<PcapPacket> handshakes = mostwanted.getThreeWayHandshakePackets();
        Element handshakeEvent = ElementCreator.getHandShakeOrFinishEvent(handshakes, xesManager, XESConstants.HANDSHAKE_CONCEPT_NAME);

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