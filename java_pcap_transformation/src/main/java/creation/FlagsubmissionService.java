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

public class FlagsubmissionService extends AbstractXESService implements IService{
    private static final String FLAGSUBMISSION="Flagsubmission";
    private static final String FLAGSUBMISSION_IP_STRING="10.16.13.37";
    private static InetAddress FLAGSUBMISSION_IP;

    static {
        try {
            FLAGSUBMISSION_IP=InetAddress.getByName(FLAGSUBMISSION_IP_STRING);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public FlagsubmissionService(String teamName, InetAddress teamIP, String teamMask) {
        super(FLAGSUBMISSION, teamName, teamIP, teamMask, FLAGSUBMISSION_IP);
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

        XESManager xesManager=new XESManager(xesPath,FLAGSUBMISSION+"_"+getTeamName());

        List<Session> handshakes= DefaultEventCreator.checkForThreeWayHandshake(packetList);
        System.out.println("Amount of handshakes "+handshakes.size());
        List<List<PcapPacket>> finishes=DefaultEventCreator.checkForFinishing(packetList);
        System.out.println("Amount of finishes "+finishes.size());
        List<Flagsubmission> flagsubmissions= FlagsubmissionEventCreator.buildFlagsubmissions(handshakes,finishes,packetList,getTeamIP(),FLAGSUBMISSION_IP);
        System.out.println("The following Flagsubmissions were detected: "+flagsubmissions.size());
        createFlagsubmissionXES(flagsubmissions,xesManager);
    }

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
