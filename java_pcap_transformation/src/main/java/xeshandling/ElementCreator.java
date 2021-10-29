package xeshandling;

import constants.HTTPConstants;
import constants.XESConstants;
import exceptions.NoMethodFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import packets.PcapPacket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementCreator {
    private static final String HANDSHAKE_CONCEPT_NAME="Established handshake";


    private static Document document;

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

    public static Element getHandShakeOrFinishEvent(List<PcapPacket> packets, XESManager xesManager) {
        PcapPacket firstPacketHandshake=packets.get(0);
        PcapPacket lastPacketHandshake=packets.get(packets.size()-1);

        HashMap<String,String> conceptNameArguments=new HashMap<>();
        conceptNameArguments.put(XESConstants.KEY_STRING,XESConstants.CONCEPT_NAME);
        conceptNameArguments.put(XESConstants.VALUE_STRING,HANDSHAKE_CONCEPT_NAME);
        Element conceptName=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, conceptNameArguments);

        HashMap<String,String> initiatorArguments=new HashMap<>();
        initiatorArguments.put(XESConstants.KEY_STRING,XESConstants.INITIATOR_STRING);
        initiatorArguments.put(XESConstants.VALUE_STRING,firstPacketHandshake.getIpSender().getHostAddress());
        Element initiatorElement=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,initiatorArguments);

        HashMap<String,String> dateArguments=new HashMap<>();
        dateArguments.put(XESConstants.KEY_STRING,XESConstants.TIME_NAME);
        dateArguments.put(XESConstants.VALUE_STRING,lastPacketHandshake.getArrivalTime().toString());
        Element dateElement=xesManager.createSimpleElement(XESConstants.DATE_ARGUMENT,dateArguments);

        ArrayList<Element> elements=new ArrayList<>();
        elements.add(conceptName);
        elements.add(initiatorElement);
        elements.add(dateElement);

        Element handshakeOrFinishEvent=xesManager.createNestedElement(XESConstants.EVENT_STRING,elements);
        return handshakeOrFinishEvent;
    }

    public Element getEventsOfPSHACK(List<PcapPacket> packets, XESManager xesManager) {

        ArrayList<Element> elements=new ArrayList<>();
        for(PcapPacket packet : packets) {
            isHTTPRequesting(packet.getTcpPayload()) {
                Element httpEvent=getHTTPEventElement(packet, xesManager);
                elements.add(httpEvent);
            }
        }
    }

    private boolean isHTTPRequesting(String payload) {
        if(payload==null) {
            return false;
        }
        if(payload.contains("HTTP")) {
            return true;
        }
        return false;
    }

    private Element getHTTPEventElement(PcapPacket packet, XESManager xesManager) {
        /*
        <event>
			<string key="concept:name" value="HTTP-Request"/>
			<string key="HTTP-method" value="GET"/>
			<string key="fullURI" value="http://10.14.1.9:2048/"/>
			<string key="requester" value="10.13.146.1"/>
			<date key="time:timestamp" value="2010-12-03T21:36:47:534798" / >
		</event>
        */
        HashMap<String, String> conceptArguments=new HashMap<>();
        conceptArguments.put(XESConstants.KEY_STRING,XESConstants.CONCEPT_NAME);
        conceptArguments.put(XESConstants.VALUE_STRING,"HTTP-Request");
        Element conceptName=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,conceptArguments);

        HashMap<String, String> methodArguments=new HashMap<>();
        methodArguments.put(XESConstants.KEY_STRING,"HTTP-method");
        String httpMethod=getHTTPMethod(packet.getTcpPayload();
        if(httpMethod==null) {
            throw new NoMethodFoundException();
        }
        methodArguments.put(XESConstants.VALUE_STRING,httpMethod);
        Element httpMethodElement=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,methodArguments);

        HashMap<String, String> uriArguments=new HashMap<>();
        uriArguments.put(XESConstants.KEY_STRING,"fullURI");
        uriArguments.put(XESConstants.VALUE_STRING, getExtractedHostAddress(packet.getTcpPayload()).getHostAddress());
        Element uriElement=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,uriArguments);

        HashMap<String, String> requesterArguments=new HashMap<>();
        requesterArguments.put(XESConstants.KEY_STRING,"requester");
        requesterArguments.put(XESConstants.VALUE_STRING,packet.getIpSender().getHostAddress());
        Element requester=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,)

        HashMap<String, String> dateArguments=new HashMap<>();
        dateArguments
    }

    private String getHTTPMethod(String payload) {
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

    private String getFirstWordRight(String payload) {
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

    private String simplyCheckContaining(String payload) {
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

    private InetAddress getExtractedHostAddress(String payload) {
        InetAddress result=null;

        payload=payload.trim();

        int index=payload.indexOf("Host:");
        String rest=payload.substring(index);
        String addressString="";

        for(int i=0; i<rest.length();++i) {
            char temp=rest.charAt(i);
            if(Character.isLetter(temp)) {
                break;
            }
            addressString+=temp;
        }
        try {
            result = InetAddress.getByName(addressString);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return result;
    }
}
