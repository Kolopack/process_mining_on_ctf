package xeshandling;

import constants.XESConstants;
import javafx.util.Pair;
import org.w3c.dom.Element;
import packets.PcapPacket;
import packets.Session;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The type Default event creator.
 * Includes all methods (mostly static) which are using algorithms for finding and checking Three-Way-Handshakes,
 * Finishes,... and so on, used by all the services together.
 * The methods declared and defined here are mainly used by all services for building their corresponding XES-files.
 */
public class DefaultEventCreator {
    /**
     * Static field which is used to filter out TCP-retransmission by checking their Payload
     */
    private static final String HTTP_CONTINUATION_STRING = "COMMAND:";

    /**
     * Check for three way handshake list.
     *
     * @param list the list with PcapPackets
     * @return the list of Session-objects of Handshakes found in this list of Packets
     */
    public static List<Session> checkForThreeWayHandshake(List<PcapPacket> list) {
        List<Session> result = new ArrayList<>();

        long seqA;
        InetAddress client;
        InetAddress server;

        for (int i = 0; i < list.size(); ++i) {

            //Current packet
            PcapPacket current = list.get(i);
            if (current.getTcpFlags().get("SYN")) {
                client = current.getIpSender();
                server = current.getIpReceiver();
                seqA = current.getSeqNumber();

                Pair<Long, PcapPacket> secondPacket = checkForSecondPacketThreeWayHandshake(list, i, server, seqA);
                Long seqB = secondPacket.getKey();

                if (seqB != null) {


                    Pair<Integer, PcapPacket> thirdPacket = checkForThirdPacketThreeWayHandshake(list, i, seqB, (seqA + 1), client);
                    if (thirdPacket.getKey() != null) {
                        List<PcapPacket> handshake = new ArrayList();
                        handshake.add(current);
                        handshake.add(secondPacket.getValue());
                        handshake.add(thirdPacket.getValue());

                        Session session = new Session();
                        session.setCertainIndex(thirdPacket.getKey());
                        session.setPackets(handshake);

                        result.add(session);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Check for second packet three way handshake pair.
     *
     * @param list     the list of PcapPackets
     * @param index    the index at which the search should start inside of the Packet-list
     * @param server   the server (The receiver of the first packet of the handshake)
     * @param seqFirst the seq first (seq-number of the first packet of the handshake)
     * @return the pair containing the index of the result-packet and the packet of the second handshake
     */
    public static Pair checkForSecondPacketThreeWayHandshake(List<PcapPacket> list, int index, InetAddress server, Long seqFirst) {
        Long result = null;

        for (int i = index + 1; i < list.size(); ++i) {
            PcapPacket packet = list.get(i);
            if (packet.getIpSender().equals(server) && packet.getAckNumber() == (seqFirst + 1)) {
                if (packet.getTcpFlags().get("SYN") && packet.getTcpFlags().get("ACK")) {
                    result = packet.getSeqNumber();
                    return new Pair(result, packet);
                }
            }
        }
        return new Pair(result, null);
    }

    /**
     * Check for third packet three way handshake pair.
     *
     * @param list      the list of PcapPackets to be analysed
     * @param index     the index at which the search should start inside of the Packet-list
     * @param seqSecond the seq second (seq-number of the second packet of the handshake)
     * @param ackSecond the ack second (the ack-number of the second packet of the handshake)
     * @param client    the client (Sender of the first packet, initiator of the TCP-handshake)
     * @return the pair containing the index of the found packet and the third handshake packet
     */
    public static Pair checkForThirdPacketThreeWayHandshake(List<PcapPacket> list, int index, Long seqSecond, Long ackSecond, InetAddress client) {


        for (int i = index + 1; i < list.size(); ++i) {
            PcapPacket packet = list.get(i);
            if (packet.getIpSender().equals(client) && packet.getAckNumber() == (seqSecond + 1)
                    && packet.getSeqNumber() == ackSecond) {
                if (packet.getTcpFlags().get("ACK")) {
                    return new Pair<>(i, packet);
                }
            }
        }
        return new Pair<>(null, null);
    }

    /**
     * Check for finishing list.
     *Method which deliveries the finishes out of the List of packets handed over
     * @param list the list of PcapPackets to be analysed
     * @return the list of finishes (List of List of PcapPackets, which are the finishes).
     */
    public static List<List<PcapPacket>> checkForFinishing(List<PcapPacket> list) {
        List<List<PcapPacket>> result = new ArrayList<>();

        long seqA;
        long ackA;
        InetAddress partnerA;
        InetAddress partnerB;

        for (int i = 0; i < list.size(); ++i) {
            PcapPacket current = list.get(i);

            if (current.getTcpFlags().get("FIN")
                    && !isHTTPContinuation(current.getIPPayload(), current.getTcpPayload())) {
                List<PcapPacket> finish = new ArrayList<>();

                partnerA = current.getIpSender();
                partnerB = current.getIpReceiver();
                seqA = current.getSeqNumber();
                ackA = current.getAckNumber();

                List<PcapPacket> rest = ListManager.getRestOfList(list, i);

                List<Pair<Long, PcapPacket>> secondPacket = checkForSecondPacketFinishing(rest, seqA, ackA, partnerB);
                if (!secondPacket.isEmpty()) {
                    finish.add(current);
                    for (Pair<Long, PcapPacket> pair : secondPacket) {
                        finish.add(pair.getValue());
                    }

                    Pair<Boolean, PcapPacket> thirdPacket = checkForThirdPacketFinishing(rest, secondPacket.get(0).getKey(), partnerA);
                    if (thirdPacket.getKey()) {
                        finish.add(thirdPacket.getValue());
                    }
                }
                if (!finish.isEmpty()) {
                    result.add(finish);
                }
            }
        }

        return result;
    }

    /**
     * Check for second packet finishing list.
     *
     * @param list     the list of PcapPackets to be analysed
     * @param seq      the seq-number of the first packet of the finish
     * @param ack      the ack-number of the first packet of the finish
     * @param partnerB IP-address of the partner b (Server) as java.net.InetAddress
     * @return the list of Pair<Long, PcapPacket> with the sequence numbers as key and the corresponding second
     * finish-packet as value.
     */
    public static List<Pair<Long, PcapPacket>> checkForSecondPacketFinishing(List<PcapPacket> list, Long seq, Long ack, InetAddress partnerB) {
        long result;
        List<Pair<Long, PcapPacket>> resultList = new ArrayList<>();

        for (PcapPacket packet : list) {
            if (packet.getIpSender().equals(partnerB) && packet.getTcpFlags().get("ACK")
                    && packet.getAckNumber() == (seq + 1) && packet.getSeqNumber() == ack
                    && !isHTTPContinuation(packet.getIPPayload(), packet.getTcpPayload())) {
                result = packet.getSeqNumber();
                resultList.add(new Pair(result, packet));
                return resultList;
            }

        }
        return resultList;
    }

    /**
     * Check for third packet finishing pair.
     *
     * @param list     the list of PcapPackets to be analysed
     * @param seqB     the seq-number of the second TCP-finishing packet
     * @param partnerA the IP-address of the partner a (the initiator of the whole finish-event)
     * @return the pair of key=boolean (is a third packet existing) and value=the PcapPacket (null when key=false)
     */
    public static Pair checkForThirdPacketFinishing(List<PcapPacket> list, Long seqB, InetAddress partnerA) {

        for (PcapPacket packet : list) {
            if (packet.getIpSender().equals(partnerA) && packet.getTcpFlags().get("ACK")
                    && packet.getAckNumber() == (seqB + 1)
                    && !isHTTPContinuation(packet.getIPPayload(), packet.getTcpPayload())) {
               return new Pair(true, packet);
            }
        }
        return new Pair(false, null);
    }

    /**
     * Is psh or ack flag set boolean.
     * Method to check if the PSH or ACK flag is set in a PcapPacket.
     * The method also checks if the packet is a HTTP-continuation-packet. It returns false if its a continuation-
     * packet
     * @param flags      the flags via HashMap<String, Boolean>, with key= f.i. "ACK" (String) and value=boolean (Set or not set)
     * @param ipPayload  the ip payload as String
     * @param tcpPayload the tcp payload as String
     * @return the boolean if PSH or ACK are set.
     */
    public static boolean isPSHOrACKFlagSet(HashMap<String, Boolean> flags,
                                             String ipPayload, String tcpPayload) {
        boolean result = false;

        if (flags.get("PSH") || flags.get("ACK")) {
            if (!flags.get("FIN") && !flags.get("SYN")) {
                result = true;
            }
            if (isHTTPContinuation(ipPayload, tcpPayload)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Method which checks if a packet is a HTTP-continuation-packet by using the payload (TCP and IP)
     * @param iPPayload the IP-payload of the packet as String
     * @param tcpPayload the TCP-payload of the packet as String
     * @return boolean whether there is HTTP-continuation inside the given payloads or not
     */
    private static boolean isHTTPContinuation(String iPPayload, String tcpPayload) {
        return iPPayload != null && iPPayload.contains(HTTP_CONTINUATION_STRING) ||
                (tcpPayload != null && tcpPayload.contains(HTTP_CONTINUATION_STRING));
    }

    /**
     * Gets service name element.
     *
     * @param xesManager  an instance of XESManager
     * @param serviceName the service name as String
     * @return the service name DOM element
     */
    public static Element getServiceNameElement(XESManager xesManager, String serviceName) {
        HashMap<String, String> serviceParameters = new HashMap<>();
        serviceParameters.put(XESConstants.KEY_STRING, "service");
        serviceParameters.put(XESConstants.VALUE_STRING, serviceName);
        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, serviceParameters);
    }

    /**
     * Gets team name element.
     *
     * @param xesManager an instance of XESManager
     * @param teamName   the team name as String
     * @return the team name DOM element
     */
    public static Element getTeamNameElement(XESManager xesManager, String teamName) {
        HashMap<String, String> teamnameParameters = new HashMap<>();
        teamnameParameters.put(XESConstants.KEY_STRING, "teamname");
        teamnameParameters.put(XESConstants.VALUE_STRING, teamName);
        return xesManager.createSimpleElement(XESConstants.STRING_ARGUMENT, teamnameParameters);
    }
}

