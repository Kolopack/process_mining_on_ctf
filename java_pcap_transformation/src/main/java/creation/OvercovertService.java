package creation;

import constants.XESConstants;
import enumerations.Finishes;
import exceptions.PacketListIsEmptyException;
import exceptions.UnavailableException;
import org.w3c.dom.Element;
import packets.PcapPacket;
import serviceRepresentation.Overcovert;
import xeshandling.DefaultEventCreator;
import xeshandling.ElementCreator;
import xeshandling.OvercovertReader;
import xeshandling.XESManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Service-class containing the methods and logic for creating the XES for the Overcovert-service
 */
public class OvercovertService extends AbstractXESService implements IService{
    /**
     * Name of the Overcovert-service as string, for setting the file-name of XES
     */
    private static final String OVERCOVERT="Overcovert";
    /**
     * The IP-address of the Service, for setting up the InetAddress-Object
     */
    private static final String OVERCOVERT_IP_STRING="10.14.1.10";
    /**
     * The IP-address of the Service as java.net.InetAddress-field
     */
    private static InetAddress OVERCOVERT_IP;

    static {
        try {
            OVERCOVERT_IP = InetAddress.getByName(OVERCOVERT_IP_STRING);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for the OvercovertService, as indicated in the abstract class (AbstractXESService)
     * @param teamName Name of the team as String
     * @param teamIP IP-address of the team as java.net.InetAddress
     * @param teamMask Subnet-mask of the team-network as a String in format (f.i. 255.255.0.0)
     */
    public OvercovertService(String teamName, InetAddress teamIP, String teamMask) {
        super(OVERCOVERT, teamName, teamIP, teamMask,OVERCOVERT_IP);
    }

    /**
     *Overridden method of the implemented interface, for creating a XES-file with given List of PcapPackets
     * @param packetList List<PcapPacket> containing the PcapPackets
     * @param xesPath The path to the XES-file to be created as a String
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
        XESManager xesManager=new XESManager(xesPath, OVERCOVERT+"_"+getTeamName());

        OvercovertReader overcovertReader=new OvercovertReader();
        List<Overcovert> overcoverts=overcovertReader.getOvercovert(packetList,getTeamIP(),getTeamMask(),OVERCOVERT_IP);

        createOvercovertXES(overcoverts,xesManager);
    }

    /**
     * Creates the XES setting up all the Overcovert-traces and tag-attributes
     * @param overcovertList List of Overcovert-objects found
     * @param xesManager Instance of XESManager, to create Java DOM-elements
     */
    private void createOvercovertXES(List<Overcovert> overcovertList, XESManager xesManager) {
        System.out.println("Nun wird XES gebaut :-)");

        Element serviceElement=DefaultEventCreator.getServiceNameElement(xesManager,OVERCOVERT);
        xesManager.addNewElementToLog(serviceElement);

        Element teamElement=DefaultEventCreator.getTeamNameElement(xesManager,getTeamName());
        xesManager.addNewElementToLog(teamElement);

        for (Overcovert mostwanted : overcovertList) {
            Element mostwantedTrace = getTraceForOvercovert(mostwanted, xesManager);
            xesManager.addNewElementToLog(mostwantedTrace);
        }

        xesManager.finishFile();
    }

    /**
     * Sets up and delivers the Trace-DOM-object for a given Overcovert-object
     * @param overcovert Object of Overcovert
     * @param xesManager Instance of XESManager
     * @return Trace for one Overcovert, as Java-DOM-Element
     */
    private Element getTraceForOvercovert(Overcovert overcovert, XESManager xesManager) {
        ArrayList<Element> headerElements=getTraceHeaderElements(overcovert,xesManager);

        List<PcapPacket> handshakeList=new ArrayList<PcapPacket>(overcovert.getHandshakes().values());
        Element handshakeElement= ElementCreator.getHandShakeOrFinishEvent(handshakeList,xesManager, XESConstants.HANDSHAKE_CONCEPT_NAME);

        List<PcapPacket> pshackList=overcovert.getInbetween();
        List<Element> pshAckElements=ElementCreator.getEventsOfPSHACK(pshackList, xesManager, OVERCOVERT_IP);

        List<HashMap<Finishes, PcapPacket>> finishesList=overcovert.getFinishes();
        ArrayList<Element> finishesElements=new ArrayList<>();

        for(HashMap<Finishes, PcapPacket> element : finishesList) {
            List<PcapPacket> temp= new ArrayList<>(element.values());
            Element finishesElement=ElementCreator.getHandShakeOrFinishEvent(temp,xesManager,XESConstants.FINISHING_CONCEPT_NAME);
            finishesElements.add(finishesElement);
        }

        Element resetElement=ElementCreator.getResetElement(overcovert.getReset(),xesManager);

        ArrayList<Element> traceElements=new ArrayList<>();
        traceElements.addAll(headerElements);
        traceElements.add(handshakeElement);
        traceElements.addAll(pshAckElements);
        traceElements.addAll(finishesElements);
        traceElements.add(resetElement);

        Element trace= xesManager.createNestedElement(XESConstants.TRACE_ARGUMENT,traceElements);
        return trace;
    }

    /**
     * Creates and returns the list of Elements which are neccessary for the header of the trace-object.
     * (So the attributes of the trace-Element before the Event-elements)
     * @param overcovert Object of Overcovert
     * @param xesManager Instance of XESManager
     * @return ArrayList of Elements containing the attribute-elements for the trace-object
     */
    private ArrayList<Element> getTraceHeaderElements(Overcovert overcovert, XESManager xesManager) {
        ArrayList<Element> result=new ArrayList<>();

        Element conceptName=getConceptName(xesManager);
        Element teamIP=getTeamIPElement(overcovert,xesManager);
        Element teamPort=getTeamPort(overcovert,xesManager);
        Element serviceIP=getServiceIP(overcovert,xesManager);
        Element servicePort=getServicePort(overcovert,xesManager);

        result.add(conceptName);
        result.add(teamIP);
        result.add(teamPort);
        result.add(serviceIP);
        result.add(servicePort);

        return result;
    }

    /**
     * Method for getting the conceptName-tag-DOM-Element for the trace
     * @param xesManager Instance of XESManager
     * @return conceptName-tag-Element
     */
    private Element getConceptName(XESManager xesManager) {
        HashMap<String, String> conceptArguments=new HashMap<>();
        conceptArguments.put(XESConstants.KEY_STRING,XESConstants.CONCEPT_NAME);
        conceptArguments.put(XESConstants.VALUE_STRING, XESConstants.OVERCOVERT_TRACE_NAME);

        Element result=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,conceptArguments);
        return result;
    }

    /**
     * Method for getting the TeamIP-tag DOM-element for the trace
     * @param overcovert Overcovert-class object
     * @param xesManager Instance of XESManager
     * @return TeamIP-tag element for the trace
     */
    private Element getTeamIPElement(Overcovert overcovert, XESManager xesManager) {
        HashMap<String, String> teamArguments=new HashMap<>();
        teamArguments.put(XESConstants.KEY_STRING, "team_ip");
        teamArguments.put(XESConstants.VALUE_STRING, overcovert.getTeamIP().getHostAddress());

        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, teamArguments);
    }

    /**
     * Method for getting the Team-port-tag DOM-element for the trace
     * @param overcovert Overcovert-class object
     * @param xesManager Instance of XESManager
     * @return DOM-element for the team-port for the trace
     */
    private Element getTeamPort(Overcovert overcovert, XESManager xesManager) {
        HashMap<String, String> portArguments=new HashMap<>();
        portArguments.put(XESConstants.KEY_STRING, "team_port");
        portArguments.put(XESConstants.VALUE_STRING, String.valueOf(overcovert.getTeamPort()));

        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, portArguments);
    }

    /**
     * Method for getting the Service-IP DOM-element for the trace
     * @param overcovert Overcovert-class object
     * @param xesManager Instance of XESManager
     * @return DOM-element for the Service-IP for the trace
     */
    private Element getServiceIP(Overcovert overcovert, XESManager xesManager) {
        HashMap<String, String> serviceArguments=new HashMap<>();
        serviceArguments.put(XESConstants.KEY_STRING, "service_ip");
        serviceArguments.put(XESConstants.VALUE_STRING, overcovert.getServiceIP().getHostAddress());

        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, serviceArguments);
    }

    /**
     * Method for getting the Service-Port DOM-element for the trace
     * @param overcovert Overcovert-class object
     * @param xesManager Instance of XESManager
     * @return DOM-element for the Service-Port for the trace
     */
    private Element getServicePort(Overcovert overcovert, XESManager xesManager) {
        HashMap<String, String> portArguments=new HashMap<>();
        portArguments.put(XESConstants.KEY_STRING, "service_port");
        portArguments.put(XESConstants.VALUE_STRING, String.valueOf(overcovert.getServicePort()));

        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, portArguments);
    }
}
