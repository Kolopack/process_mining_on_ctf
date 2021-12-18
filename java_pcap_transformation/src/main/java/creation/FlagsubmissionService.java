package creation;

import constants.XESConstants;
import exceptions.PacketListIsEmptyException;
import exceptions.UnavailableException;
import org.w3c.dom.Element;
import packets.PcapPacket;
import packets.Session;
import serviceRepresentation.Flagsubmission;
import xeshandling.DefaultEventCreator;
import xeshandling.ElementCreator;
import xeshandling.FlagsubmissionEventCreator;
import xeshandling.XESManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which contains the logic of creating the XES-file out of the List<PcapPacket>
 */
public class FlagsubmissionService extends AbstractXESService implements IService{
    /**
     * String-name of the flagsubmission-service, used for building the file-name
     */
    private static final String FLAGSUBMISSION="Flagsubmission";
    /**
     * The IP-address of the flagsubmission-service as a String, used for building the InetAdress-object
     */
    private static final String FLAGSUBMISSION_IP_STRING="10.16.13.37";
    /**
     * The IP-address of the flagsubmission-service as java.net.InetAddress, used for building it into the XES-files
     */
    private static InetAddress FLAGSUBMISSION_IP;

    static {
        try {
            FLAGSUBMISSION_IP=InetAddress.getByName(FLAGSUBMISSION_IP_STRING);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for FlagsubmissionService, as indicated by the abstract class (AbstractXESService)
     * @param teamName Name of the team as String
     * @param teamIP IP-address of the team as java.net.InetAddress
     * @param teamMask the subnet-mask of the team as a String in the format (f.i. 255.255.0.0)
     */
    public FlagsubmissionService(String teamName, InetAddress teamIP, String teamMask) {
        super(FLAGSUBMISSION, teamName, teamIP, teamMask, FLAGSUBMISSION_IP);
    }

    /**
     * Overridden method of the implemented interface
     * @param packetList List of PcapPackets
     * @param xesPath The path for the XES-file to be created
     */
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

        XESManager xesManager=new XESManager(xesPath,FLAGSUBMISSION+"_"+getTeamName());

        List<Session> handshakes= DefaultEventCreator.checkForThreeWayHandshake(packetList);
        System.out.println("Amount of handshakes "+handshakes.size());
        List<List<PcapPacket>> finishes=DefaultEventCreator.checkForFinishing(packetList);
        System.out.println("Amount of finishes "+finishes.size());
        List<Flagsubmission> flagsubmissions= FlagsubmissionEventCreator.buildFlagsubmissions(handshakes,finishes,packetList,getTeamIP(),FLAGSUBMISSION_IP);
        System.out.println("The following Flagsubmissions were detected: "+flagsubmissions.size());
        createFlagsubmissionXES(flagsubmissions,xesManager);
    }

    /**
     *Creates the XES including all the Flagsubmission-traces, setting up the primary tags.
     * @param flagsubmissions List of found flagsubmissions, to create the XES-representations to
     * @param xesManager The instance of XESManager
     */
    private void createFlagsubmissionXES(List<Flagsubmission> flagsubmissions, XESManager xesManager) {
        Element serviceName=DefaultEventCreator.getServiceNameElement(xesManager,FLAGSUBMISSION);
        xesManager.addNewElementToLog(serviceName);

        Element teamName=DefaultEventCreator.getTeamNameElement(xesManager,getTeamName());
        xesManager.addNewElementToLog(teamName);

        for(Flagsubmission flagsubmission : flagsubmissions) {
            Element flagsubmissionTrace=getTraceForFlagsubmission(flagsubmission,xesManager);
            xesManager.addNewElementToLog(flagsubmissionTrace);
        }
        xesManager.finishFile();
    }

    /**
     * Creates one Trace-object for a Flagsubmission-object
     * @param flagsubmission A flagsubmission object where we want the Trace-element to
     * @param xesManager An object of XESManager
     * @return Element-instance, the full representation of a flagsubmission-trace in Java DOM
     */
    private Element getTraceForFlagsubmission(Flagsubmission flagsubmission, XESManager xesManager) {
        List<PcapPacket> handshakes=flagsubmission.getHandshakes();
        Element handshakeEvent=ElementCreator.getHandShakeOrFinishEvent(handshakes,xesManager, XESConstants.HANDSHAKE_CONCEPT_NAME);

        ArrayList<Element> inbetweenElements=getInbetweenElements(flagsubmission.getPacketsInbetween(),xesManager);

        List<PcapPacket> finishes=flagsubmission.getFinishes();
        Element finishesEvent=ElementCreator.getHandShakeOrFinishEvent(finishes,xesManager,XESConstants.FINISHING_CONCEPT_NAME);

        ArrayList<Element> elements=new ArrayList<>();
        elements.add(handshakeEvent);
        elements.addAll(inbetweenElements);
        elements.add(finishesEvent);
        Element trace= xesManager.createNestedElement(XESConstants.TRACE_ARGUMENT, elements);

        return trace;
    }

    /**
     * Method for receiving the List of Element-object representing the Events inside the packets which are
     * between handshake and TCP-finishing.
     * @param packets The List of PcapPackets which are inbetween
     * @param xesManager Object of XESManager, to build DOM-elements
     * @return ArrayList<Element> containing all created elements out of the packets
     */
    private static ArrayList<Element> getInbetweenElements(List<PcapPacket> packets, XESManager xesManager) {
        ArrayList<Element> result=new ArrayList<>();

        for(PcapPacket packet : packets) {
            if(ElementCreator.isHTTPRequesting(packet.getTcpPayload())) {
                Element httpEvent=ElementCreator.getHTTPEventElement(packet,xesManager);
                if(httpEvent!=null) {
                    result.add(httpEvent);
                }
            }
        }
        return result;
    }
}
