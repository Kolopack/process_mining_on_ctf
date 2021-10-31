package xeshandling;

import constants.HTTPConstants;
import constants.XESConstants;
import exceptions.NoMethodFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import packets.PcapPacket;

import javax.xml.stream.events.EndElement;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementCreator {

    private static Document document;
    private static final String EXTRACTINGINDEXSTRING="Host:";

    public ElementCreator(Document document) {
        this.document=document;
    }

    public static Element createSimpleElement(String name, HashMap<String, String> parameters) {
        Element result=document.createElement(name);
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            result.setAttribute(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static Element createNestedElement(String name, ArrayList<Element> children) {
        Element result=document.createElement(name);
        for(Element elem : children) {
            result.appendChild(elem);
        }
        return result;
    }

    public static Element getHandShakeOrFinishEvent(List<PcapPacket> packets, XESManager xesManager, String conceptName) {
        PcapPacket firstPacketHandshake=packets.get(0);
        PcapPacket lastPacketHandshake=packets.get(packets.size()-1);

        HashMap<String,String> conceptNameArguments=new HashMap<>();
        conceptNameArguments.put(XESConstants.KEY_STRING,XESConstants.CONCEPT_NAME);
        conceptNameArguments.put(XESConstants.VALUE_STRING,conceptName);
        Element conceptElement=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, conceptNameArguments);

        Element initiatorElement=getRequesterOrInitiator(firstPacketHandshake,xesManager);

        Element dateElement=getDateElement(lastPacketHandshake,xesManager);

        ArrayList<Element> elements=new ArrayList<>();
        elements.add(conceptElement);
        elements.add(initiatorElement);
        elements.add(dateElement);

        Element handshakeOrFinishEvent=xesManager.createNestedElement(XESConstants.EVENT_STRING,elements);
        return handshakeOrFinishEvent;
    }

    public static ArrayList<Element> getEventsOfPSHACK(List<PcapPacket> packets, XESManager xesManager) {

        ArrayList<Element> result=new ArrayList<>();
        for(PcapPacket packet : packets) {
            if(isHTTPRequesting(packet.getTcpPayload())) {
                Element httpEvent=getHTTPEventElement(packet, xesManager);
                result.add(httpEvent);
            }
        }
        return result;
    }

    private static boolean isHTTPRequesting(String payload) {
        if(payload==null) {
            return false;
        }
        //We only want HTTP-REST requests, but not the ones which are just requesting of favicons
        if(payload.contains("HTTP") && !payload.contains("favicon")) {
            return true;
        }
        return false;
    }

    private static Element getHTTPEventElement(PcapPacket packet, XESManager xesManager) {
        HashMap<String, String> conceptArguments=new HashMap<>();
        conceptArguments.put(XESConstants.KEY_STRING,XESConstants.CONCEPT_NAME);
        conceptArguments.put(XESConstants.VALUE_STRING,"HTTP-Request");
        Element conceptName=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,conceptArguments);

        HashMap<String, String> methodArguments=new HashMap<>();
        methodArguments.put(XESConstants.KEY_STRING,"HTTP-method");
        String httpMethod=getHTTPMethod(packet.getTcpPayload());
        if(httpMethod==null) {
            throw new NoMethodFoundException();
        }
        methodArguments.put(XESConstants.VALUE_STRING,httpMethod);
        Element httpMethodElement=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,methodArguments);

        HashMap<String, String> uriArguments=new HashMap<>();
        uriArguments.put(XESConstants.KEY_STRING,"fullURI");
        uriArguments.put(XESConstants.VALUE_STRING, getExtractedHostAddress(packet.getTcpPayload()).getHostAddress());
        Element uriElement=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,uriArguments);

        Element requester=getRequesterOrInitiator(packet,xesManager);

        Element date=getDateElement(packet,xesManager);

        ArrayList<Element> elements=new ArrayList<>();
        elements.add(conceptName);
        elements.add(httpMethodElement);
        elements.add(uriElement);
        elements.add(requester);
        elements.add(date);

        Element result=xesManager.createNestedElement(XESConstants.EVENT_STRING,elements);
        return result;
    }

    private static String getHTTPMethod(String payload) {
        String result=getFirstWordRight(payload);
        if(result!=null) {
            return result;
        }

        result=simplyCheckContaining(payload);
        if(result!=null) {
            return result;
        }
        return null;
    }

    private static String getFirstWordRight(String payload) {
        String firstWord=payload.substring(0, payload.indexOf(' '));

        switch (firstWord) {
            case HTTPConstants.GET:
                return HTTPConstants.GET;
            case HTTPConstants.DELETE:
                return HTTPConstants.DELETE;
            case HTTPConstants.POST:
                return HTTPConstants.POST;
            case HTTPConstants.PUT:
                return HTTPConstants.PUT;
        }
        return null;
    }

    private static String simplyCheckContaining(String payload) {
        if(payload.contains(HTTPConstants.GET)) {
            return HTTPConstants.GET;
        }
        if(payload.contains(HTTPConstants.POST)) {
            return HTTPConstants.POST;
        }
        if(payload.contains(HTTPConstants.PUT)) {
            return HTTPConstants.PUT;
        }
        if (payload.contains(HTTPConstants.DELETE)) {
            return HTTPConstants.DELETE;
        }
        return null;
    }

    private static InetAddress getExtractedHostAddress(String payload) {
        InetAddress result=null;

        payload=payload.trim();

        int index=payload.indexOf(EXTRACTINGINDEXSTRING);
        String rest=payload.substring(index+EXTRACTINGINDEXSTRING.length()+1);
        System.out.println("Rest: "+rest);
        String addressString="";

        for(int i=0; i<rest.length();++i) {
            char temp=rest.charAt(i);
            if(Character.isLetter(temp) || temp==':') {
                break;
            }
            addressString+=temp;
        }
        try {
            System.out.println("AddressString: "+addressString);
            result = InetAddress.getByName(addressString);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Element getDateElement(PcapPacket packet, XESManager xesManager) {
        HashMap<String, String> dateArguments=new HashMap<>();
        dateArguments.put(XESConstants.KEY_STRING,XESConstants.TIME_NAME);
        dateArguments.put(XESConstants.VALUE_STRING,packet.getArrivalTime().toString());
        Element result=xesManager.createSimpleElement(XESConstants.DATE_ARGUMENT,dateArguments);
        return result;
    }

    private static Element getRequesterOrInitiator(PcapPacket packet, XESManager xesManager) {
        HashMap<String, String> arguments=new HashMap<>();
        arguments.put(XESConstants.KEY_STRING,XESConstants.INITIATOR_STRING);
        arguments.put(XESConstants.VALUE_STRING,packet.getIpSender().getHostAddress());
        Element result=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,arguments);
        return result;
    }
}
