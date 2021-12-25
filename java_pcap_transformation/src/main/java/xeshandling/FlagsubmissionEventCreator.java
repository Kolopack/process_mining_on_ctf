package xeshandling;

import constants.XESConstants;
import org.w3c.dom.Element;
import packets.PcapPacket;
import packets.Session;
import serviceRepresentation.Flagsubmission;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The type Flagsubmission event creator.
 * Contains methods for creating Java DOM-Events specific to the Flagsubmission-Service
 */
public class FlagsubmissionEventCreator {

    /**
     * Build flagsubmissions list.
     *
     * @param handshakes List of Session-objects, each one stands for a handshakes
     * @param finishes   List of List of PcapPackets, each entry stands for one finish
     * @param packetList the packet list containing all PcapPackets
     * @return the list of Flagsubmissions found and created
     */
    public static List<Flagsubmission> buildFlagsubmissions(List<Session> handshakes, List<List<PcapPacket>> finishes,
                                                            List<PcapPacket> packetList) {
        List<Flagsubmission> result=new ArrayList<>();

        for (int i = 0; i < handshakes.size() && i < finishes.size(); ++i) {
            Flagsubmission flagsubmission=new Flagsubmission();

            List<PcapPacket> handshakePackets = handshakes.get(i).getPackets();
            int indexFirstPacketHandshake = handshakes.get(i).getCertainIndex();

            List<PcapPacket> finishesPackets = finishes.get(i);
            PcapPacket firstPacketFinishes = finishes.get(i).get(0);
            Timestamp timestampFinish = firstPacketFinishes.getArrivalTime();

            List<PcapPacket> inbetween = new ArrayList<>();

            for (int j = indexFirstPacketHandshake; j < packetList.size(); ++j) {
                PcapPacket current = packetList.get(j);
                if (!(current.getArrivalTime().before(timestampFinish))) {
                    break;
                }
                 inbetween.add(current);
            }
            if (!inbetween.isEmpty()) {
                flagsubmission.setHandshakes(handshakePackets);
                flagsubmission.setPacketsInbetween(inbetween);
                flagsubmission.setFinishes(finishesPackets);

                result.add(flagsubmission);
            }
        }
        return result;
    }

    /**
     * TODO:
     * Method for Getting a created Event of Flagsubmitting
     * @param xesManager instance of XESManager
     * @return the created DOM-Element
     */
    private Element getFlagSubmittingElement(XESManager xesManager) {
        Element conceptName=getConceptName(xesManager);
        return null;
    }

    /**
     * Method for getting the created DOM-element for the concept-name-tag of this service
     * @param xesManager instance of XESManager
     * @return the created DOM-Element
     */
    private Element getConceptName(XESManager xesManager) {
        HashMap<String, String> conceptArguments=new HashMap<>();
        conceptArguments.put(XESConstants.KEY_STRING,XESConstants.CONCEPT_NAME);
        conceptArguments.put(XESConstants.VALUE_STRING, "Submit flag");

        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT,conceptArguments);
    }
}
