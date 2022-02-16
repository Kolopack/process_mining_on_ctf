package xeshandling;

import constants.HTTPConstants;
import constants.XESConstants;
import creation.MostwantedService;
import org.w3c.dom.Element;
import packets.PcapPacket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * The type Element creator.
 * In this class Elements which are default and so used by all services are served.
 * So static methods are common here, where default Elements can be created.
 */
public class ElementCreator {

    private static final String EXTRACTINGINDEXSTRING = "Host:";

    /**
     * Instantiates a new Element creator.
     */
    public ElementCreator() {
    }

    /**
     * Gets hand shake or finish event.
     *
     * @param packets     the packets to be analysed
     * @param xesManager  instance of XESManager
     * @param conceptName the concept name to be used for the Element
     * @return The DOM-element representing the Handshake- or Finish-event
     */
    public static Element getHandShakeOrFinishEvent(List<PcapPacket> packets, XESManager xesManager, String conceptName) {
        PcapPacket firstPacketHandshake = packets.get(0);
        PcapPacket lastPacketHandshake = packets.get(packets.size() - 1);

        Element initiatorElement = null;
        if (firstPacketHandshake != null) {
            initiatorElement = getRequesterOrInitiator(firstPacketHandshake, xesManager, XESConstants.INITIATOR_STRING);
        }
        Element dateElement = null;
        if (lastPacketHandshake != null) {
            dateElement = getDateElement(lastPacketHandshake, xesManager);
        }

        ArrayList<Element> elements = new ArrayList<>();

        HashMap<String, String> conceptParameters=new HashMap<>();
        conceptParameters.put(XESConstants.KEY_STRING, XESConstants.CONCEPT_NAME);
        conceptParameters.put(XESConstants.VALUE_STRING, conceptName);
        Element conceptElement=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,conceptParameters);
        elements.add(conceptElement);

        if (initiatorElement != null) {
            elements.add(initiatorElement);
        }
        if (dateElement != null) {
            elements.add(dateElement);
        }

        Element result=xesManager.createNestedElement(XESConstants.EVENT_STRING, elements);
        result.setAttribute(XESConstants.KEY_STRING, XESConstants.CONCEPT_NAME);
        result.setAttribute(XESConstants.VALUE_STRING, conceptName);
        return result;
    }

    /**
     * Gets events of pshack.
     *
     * @param packets    the packets to be analysed
     * @param xesManager instance of XESManager
     * @return ArrayList containing the Elements found and created out of the Packet-list
     */
    public static ArrayList<Element> getEventsOfPSHACK(List<PcapPacket> packets, XESManager xesManager, InetAddress serviceIP) {

        ArrayList<Element> result = new ArrayList<>();
        for (PcapPacket packet : packets) {
            if (isHTTPRequesting(packet.getTcpPayload())) {
                Element httpEvent = getHTTPEventElement(packet, xesManager);
                result.add(httpEvent);
            }

        }
        ArrayList<Element> pshattacks = getPSHACKEvents(packets, xesManager, serviceIP);
        result.addAll(pshattacks);
        return result;
    }

    /**
     * Is http requesting boolean.
     *Checks whether a given payload contains HTTP-requests or not
     * @param payload the payload of a packet as String
     * @return the boolean indicating whether the payload contained a HTTP-requesting or not
     */
    public static boolean isHTTPRequesting(String payload) {
        if (payload == null) {
            return false;
        }
        //We only want HTTP-REST requests, but not the ones which are just requesting of favicons
        return (payload.contains(HTTPConstants.HTTP) || payload.contains(HTTPConstants.GET) ||
                payload.contains(HTTPConstants.POST))
                && !payload.contains("favicon");
    }

    /**
     * Gets http event element.
     *Returns the created Element for representing an HTTP-event
     * @param packet     the packet which contains the HTTP-requesting
     * @param xesManager instance of XESManager
     * @return the http event DOM-element
     */
    public static Element getHTTPEventElement(PcapPacket packet, XESManager xesManager) {
        String httpMethod = getHTTPMethod(packet.getTcpPayload());
        if (httpMethod == null) {
            return null;
        }
        if(httpMethod.equals(HTTPConstants.POST)) {
            return getPostSubmitEvent(httpMethod,packet,xesManager);
        }
        HashMap<String, String> conceptArguments=new HashMap<>();
        conceptArguments.put(XESConstants.KEY_STRING, XESConstants.CONCEPT_NAME);
        conceptArguments.put(XESConstants.VALUE_STRING, "HTTP-Request");
        Element conceptElement=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,conceptArguments);

        HashMap<String, String> methodArguments = new HashMap<>();
        methodArguments.put(XESConstants.KEY_STRING, "HTTP-method");
        methodArguments.put(XESConstants.VALUE_STRING, httpMethod);
        Element httpMethodElement = xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, methodArguments);

        Element uriElement = getURIElement(packet,xesManager);

        Element requester = getRequesterOrInitiator(packet, xesManager, XESConstants.INITIATOR_STRING);

        Element date = getDateElement(packet, xesManager);

        ArrayList<Element> elements = new ArrayList<>();
        elements.add(conceptElement);
        elements.add(httpMethodElement);
        elements.add(uriElement);
        elements.add(requester);
        elements.add(date);

        Element result=xesManager.createNestedElement(XESConstants.EVENT_STRING, elements);
        result.setAttribute(XESConstants.KEY_STRING, XESConstants.CONCEPT_NAME);
        result.setAttribute(XESConstants.VALUE_STRING, "HTTP-Request");
        return result;
    }

    /**
     * Method for getting an HTTP-event which represents a POST-request (including the then changed attributes)
     * @param httpMethod The HTTP-method as String
     * @param packet the packet containing the request
     * @param xesManager instance of XESManager
     * @return an Java DOM-element representing the POST-HTTP-Requesting
     */
    private static Element getPostSubmitEvent(String httpMethod,PcapPacket packet, XESManager xesManager) {
        HashMap<String, String> conceptArguments = new HashMap<>();
        conceptArguments.put(XESConstants.KEY_STRING, XESConstants.CONCEPT_NAME);
        conceptArguments.put(XESConstants.VALUE_STRING, "Submit flag");
        Element conceptName= xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,conceptArguments);

        HashMap<String, String> methodArguments=new HashMap<>();
        methodArguments.put(XESConstants.VALUE_STRING, httpMethod);
        Element httpMethodElement = xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, methodArguments);

        Element uriElement= getURIElement(packet,xesManager);

        Element requestor=getRequesterOrInitiator(packet,xesManager,XESConstants.REQUESTER_STRING);

        //TODO: flagstring
        System.out.println("Here read out flagstring");

        Element dateElement=getDateElement(packet,xesManager);

        ArrayList<Element> elements=new ArrayList<>();
        elements.add(conceptName);
        elements.add(httpMethodElement);
        elements.add(uriElement);
        elements.add(requestor);
        elements.add(dateElement);
        return xesManager.createNestedElement(XESConstants.EVENT_STRING,elements);
    }

    /**
     * Method which checks a List of packets and extracts the PSH-ACK-attack events out of them
     * @param packets The list of packets to be analysed
     * @param xesManager instance of XESManager
     * @return ArrayList containing all created DOM-Elements which indicate PSH-ACK-attacks
     */
    private static ArrayList<Element> getPSHACKEvents(List<PcapPacket> packets, XESManager xesManager, InetAddress serviceIP) {
        ArrayList<Element> result=new ArrayList<>();

        for(int i=0; i<packets.size();++i) {
            PcapPacket current=packets.get(i);
            boolean isAttack=false;
            if(!current.getIpSender().equals(serviceIP)) {
                isAttack=true;
            }
            if(current.getTcpFlags().get("PSH") && current.getTcpFlags().get("ACK")) {
                if(i+1!= packets.size()) {
                    PcapPacket next=packets.get(i+1);
                    if(next.getTcpFlags().get("ACK") && !next.getTcpFlags().get("PSH")) {

                        /*HashMap<String, String> conceptParameters=new HashMap<>();
                        conceptParameters.put(XESConstants.KEY_STRING, XESConstants.CONCEPT_NAME);
                        if(isAttack) {
                            conceptParameters.put(XESConstants.VALUE_STRING, XESConstants.EXECUTING_ATTACK_NAME);
                        }
                        else {
                            conceptParameters.put(XESConstants.VALUE_STRING,XESConstants.INTERACTING_TO_ATTACK);
                        }
                        Element conceptElement=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,conceptParameters);
                        attackElements.add(conceptElement);*/

                        Element element=createPSHACKEvent(current,xesManager, isAttack);
                        result.add(element);
                        Element receivedACKElement= MostwantedService.getReceiveAckElement(next, xesManager);
                        result.add(receivedACKElement);
                    }
                }
                else {
                    /*HashMap<String, String> conceptParameters=new HashMap<>();
                    conceptParameters.put(XESConstants.KEY_STRING, XESConstants.CONCEPT_NAME);
                    if(isAttack) {
                        conceptParameters.put(XESConstants.VALUE_STRING, XESConstants.EXECUTING_ATTACK_NAME);
                    }
                    else {
                        conceptParameters.put(XESConstants.VALUE_STRING,XESConstants.INTERACTING_TO_ATTACK);
                    }
                    Element conceptElement=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,conceptParameters);
                    attackElements.add(conceptElement);*/

                    Element element=createPSHACKEvent(current,xesManager, isAttack);
                    result.add(element);
                    Element receivedACKElement= MostwantedService.getReceiveAckElement(null, xesManager);
                    result.add(receivedACKElement);
                }
            }
        }
        return result;
    }

    /**
     * This method is called by the getPSHACKEvents-method for building the Java DOM-elements
     * @param pshack PcapPacket which contains a PSH
     * @param xesManager instance of XESManager
     * @return DOM-element representing one PSHACK-attack
     */
    private static Element createPSHACKEvent(PcapPacket pshack, XESManager xesManager, boolean isAttack) {
        HashMap<String, String> conceptParameters=new HashMap<>();
        conceptParameters.put(XESConstants.KEY_STRING, XESConstants.CONCEPT_NAME);
        if(isAttack) {
            conceptParameters.put(XESConstants.VALUE_STRING, XESConstants.EXECUTING_ATTACK_NAME);
        }
        else{
            conceptParameters.put(XESConstants.VALUE_STRING,XESConstants.INTERACTING_TO_ATTACK);
        }

        Element conceptElement=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,conceptParameters);

        Element sender=getRequesterOrInitiator(pshack,xesManager,XESConstants.SENDER_STRING);

        Element receiver=getDestinationOrReceiver(pshack,xesManager);

        Element dateElement;
        dateElement = getDateElement(pshack, xesManager);
        ArrayList<Element> elements=new ArrayList<>();
        elements.add(conceptElement);
        elements.add(sender);
        elements.add(receiver);
        elements.add(dateElement);

        Element result=xesManager.createNestedElement(XESConstants.EVENT_STRING,elements);
        result.setAttribute(XESConstants.KEY_STRING, XESConstants.CONCEPT_NAME);
        if(isAttack) {
            result.setAttribute(XESConstants.VALUE_STRING, XESConstants.EXECUTING_ATTACK_NAME);
        }
        else{
            result.setAttribute(XESConstants.VALUE_STRING, XESConstants.INTERACTING_TO_ATTACK);
        }
        return result;
    }

    /**
     * Method for extracting the exact HTTP-method used in a HTTP-request
     * @param payload the payload of a PcapPacket as String
     * @return the HTTP-method as String
     */
    private static String getHTTPMethod(String payload) {
        String result = getContainedHTTPMethod(payload);
        if (result != null) {
            return result;
        }
        result = simplyCheckContaining(payload);
        return result;
    }

    /**
     * Method called by getHTTPMethod(), for extracting the Word of HTTP-Method out of the long payload.
     * @param payload The payload of a PcapPacket as String
     * @return The first word (the method) as a String
     */
    private static String getContainedHTTPMethod(String payload) {
        /*String firstWord = payload.substring(0, payload.indexOf(' '));

        return switch (payload.contains()) {
            case HTTPConstants.GET -> HTTPConstants.GET;
            case HTTPConstants.POST -> HTTPConstants.POST;
            default -> null;
        };*/
        if(payload.contains(HTTPConstants.GET)) {
            return HTTPConstants.GET;
        }
        if(payload.contains(HTTPConstants.POST)) {
            return HTTPConstants.POST;
        }
        return null;
    }

    /**
     *Method which simply checks if a certain HTTP-request-method is contained in a given payload.
     * This method is called when the other, more precise methods can not find a result.
     * @param payload of a packet as a String
     * @return The String if an HTTP-method was found, null otherwise
     */
    private static String simplyCheckContaining(String payload) {
        if (payload.contains(HTTPConstants.GET)) {
            return HTTPConstants.GET;
        }
        if (payload.contains(HTTPConstants.POST)) {
            return HTTPConstants.POST;
        }
        return null;
    }

    /**
     * Method to get a host-address (IP-address) out of the Payload of a packet, for creating corresponding
     * DOM-tags.
     * @param payload of a packet as String
     * @return found IP-address as java.net.InetAddress
     */
    private static InetAddress getExtractedHostAddress(String payload) {
        InetAddress result = null;

        payload = payload.trim();

        int index = payload.indexOf(EXTRACTINGINDEXSTRING);
        String rest = payload.substring(index + EXTRACTINGINDEXSTRING.length() + 1);
        StringBuilder addressString = new StringBuilder();

        for (int i = 0; i < rest.length(); ++i) {
            char temp = rest.charAt(i);
            if (Character.isLetter(temp) || temp == ':') {
                break;
            }
            addressString.append(temp);
        }
        try {
            result = InetAddress.getByName(addressString.toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Method which builds and returns a Date-element as found as attribute in many event-tags
     * @param packet PcapPacket which contains the Timestamp-data
     * @param xesManager Instance of XESManager
     * @return created Java DOM-Element
     */
    public static Element getDateElement(PcapPacket packet, XESManager xesManager) {
        HashMap<String, String> dateArguments = new HashMap<>();
        dateArguments.put(XESConstants.KEY_STRING, XESConstants.TIME_NAME);
        dateArguments.put(XESConstants.VALUE_STRING, packet.getArrivalTime().toString());
        return xesManager.createSimpleElement(XESConstants.DATE_ARGUMENT, dateArguments);
    }

    /**
     * Method for getting a created DOM-Element for the Requestor/Initiator of an event
     * @param packet the PcapPacket-object holding the required information
     * @param xesManager instance of XESManager
     * @param description Description to build (f.i. Initiator or Requestor)
     * @return the created DOM-element with given properties
     */
    private static Element getRequesterOrInitiator(PcapPacket packet, XESManager xesManager, String description) {
        HashMap<String, String> arguments = new HashMap<>();
        arguments.put(XESConstants.KEY_STRING, description);
        arguments.put(XESConstants.VALUE_STRING, packet.getIpSender().getHostAddress());
        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, arguments);
    }

    /**
     * Method for getting created Destination/Receiver-Element-Tag for DOM
     * @param packet the PcapPacket holding the required information
     * @param xesManager instance of XESManager
     * @return the created DOM-Element for the Destination/Receiver-tag
     */
    private static Element getDestinationOrReceiver(PcapPacket packet, XESManager xesManager) {
        HashMap<String, String> arguments = new HashMap<>();
        arguments.put(XESConstants.KEY_STRING, XESConstants.RECEIVER_STRING);
        arguments.put(XESConstants.VALUE_STRING, packet.getIpReceiver().getHostAddress());
        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, arguments);
    }

    /**
     * Gets reset element out of given properties
     *
     * @param packet     the packet holding the required information
     * @param xesManager instance of XESManager
     * @return the reset element as created with the given properties
     */
    public static Element getResetElement(PcapPacket packet, XESManager xesManager) {
        HashMap<String, String> conceptArguments = new HashMap<>();
        conceptArguments.put(XESConstants.KEY_STRING, XESConstants.CONCEPT_NAME);
        conceptArguments.put(XESConstants.VALUE_STRING, "Closed by Reset-Flag");
        Element conceptName = xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, conceptArguments);

        Element sender=getRequesterOrInitiator(packet,xesManager,XESConstants.SENDER_STRING);

        Element timestamp=getDateElement(packet,xesManager);

        ArrayList<Element> elements=new ArrayList<>();
        elements.add(conceptName);
        elements.add(sender);
        elements.add(timestamp);

        return xesManager.createNestedElement(XESConstants.EVENT_STRING, elements);
    }

    /**
     * Method for getting created URI-Element with given properties (used f.i. in Flagsubmission-XES)
     * @param packet PcapPacket holding the information
     * @param xesManager instance of XESManager
     * @return the created Element
     */
    private static Element getURIElement(PcapPacket packet, XESManager xesManager) {
        HashMap<String, String> postURIArguments=new HashMap<>();
        postURIArguments.put(XESConstants.KEY_STRING, "fullURI");
        postURIArguments.put(XESConstants.VALUE_STRING, getExtractedHostAddress(packet.getTcpPayload()).getHostAddress());
        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, postURIArguments);
    }
}
