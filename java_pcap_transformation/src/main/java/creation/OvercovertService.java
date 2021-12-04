package creation;

import constants.XESConstants;
import enumerations.Finishes;
import enumerations.Handshakes;
import enumerations.OvercovertPart;
import exceptions.PacketListIsEmptyException;
import exceptions.UnavailableException;
import org.w3c.dom.Element;
import packets.PcapPacket;
import serviceRepresentation.Mostwanted;
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

        createOvercovertXES(overcoverts,xesManager);
    }

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

    private Element getTraceForOvercovert(Overcovert overcovert, XESManager xesManager) {
        ArrayList<Element> headerElements=getTraceHeaderElements(overcovert,xesManager);

        List<PcapPacket> handshakeList=new ArrayList<PcapPacket>(overcovert.getHandshakes().values());
        Element handshakeElement= ElementCreator.getHandShakeOrFinishEvent(handshakeList,xesManager, XESConstants.HANDSHAKE_CONCEPT_NAME);

        List<PcapPacket> pshackList=overcovert.getInbetween();
        List<Element> pshAckElements=ElementCreator.getEventsOfPSHACK(pshackList, xesManager);

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

    private Element getConceptName(XESManager xesManager) {
        HashMap<String, String> conceptArguments=new HashMap<>();
        conceptArguments.put(XESConstants.KEY_STRING,XESConstants.CONCEPT_NAME);
        conceptArguments.put(XESConstants.VALUE_STRING, XESConstants.OVERCOVERT_TRACE_NAME);

        Element result=xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,conceptArguments);
        return result;
    }

    private Element getTeamIPElement(Overcovert overcovert, XESManager xesManager) {
        HashMap<String, String> teamArguments=new HashMap<>();
        teamArguments.put(XESConstants.KEY_STRING, "team_ip");
        teamArguments.put(XESConstants.VALUE_STRING, overcovert.getTeamIP().getHostAddress());

        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, teamArguments);
    }

    private Element getTeamPort(Overcovert overcovert, XESManager xesManager) {
        HashMap<String, String> portArguments=new HashMap<>();
        portArguments.put(XESConstants.KEY_STRING, "team_port");
        portArguments.put(XESConstants.VALUE_STRING, String.valueOf(overcovert.getTeamPort()));

        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, portArguments);
    }

    private Element getServiceIP(Overcovert overcovert, XESManager xesManager) {
        HashMap<String, String> serviceArguments=new HashMap<>();
        serviceArguments.put(XESConstants.KEY_STRING, "service_ip");
        serviceArguments.put(XESConstants.VALUE_STRING, overcovert.getServiceIP().getHostAddress());

        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, serviceArguments);
    }

    private Element getServicePort(Overcovert overcovert, XESManager xesManager) {
        HashMap<String, String> portArguments=new HashMap<>();
        portArguments.put(XESConstants.KEY_STRING, "service_port");
        portArguments.put(XESConstants.VALUE_STRING, String.valueOf(overcovert.getServicePort()));

        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, portArguments);
    }
}
