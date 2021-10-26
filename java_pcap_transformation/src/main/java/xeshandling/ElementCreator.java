package xeshandling;

import constants.XESConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import packets.PcapPacket;

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
}
