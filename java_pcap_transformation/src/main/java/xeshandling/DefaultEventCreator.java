package xeshandling;

import javafx.util.Pair;
import packets.PcapPacket;
import packets.Session;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class DefaultEventCreator {
    private static Logger logger = Logger.getLogger(DefaultEventCreator.class.getName());
    private static final String HTTP_CONTINUATION_STRING = "COMMAND:";

    public static List<Session> checkForThreeWayHandshake(List<PcapPacket> list) {
        List<Session> result = new ArrayList<>();

        Long seqA;
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

    public static List<List<PcapPacket>> checkForFinishing(List<PcapPacket> list) {
        List<List<PcapPacket>> result = new ArrayList<>();

        Long seqA;
        Long ackA;
        InetAddress partnerA;
        InetAddress partnerB;

        for (int i = 0; i < list.size(); ++i) {
            PcapPacket current = list.get(i);

            if (current.getTcpFlags().get("FIN")
                    && !isHTTPContinuation(current.getiPPayload(), current.getTcpPayload())) {
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

    public static List<Pair<Long, PcapPacket>> checkForSecondPacketFinishing(List<PcapPacket> list, Long seq, Long ack, InetAddress partnerB) {
        Long result;
        List<Pair<Long, PcapPacket>> resultList = new ArrayList<>();

        for (PcapPacket packet : list) {
            if (packet.getIpSender().equals(partnerB) && packet.getTcpFlags().get("ACK")
                    && packet.getAckNumber() == (seq + 1) && packet.getSeqNumber() == ack
                    && !isHTTPContinuation(packet.getiPPayload(), packet.getTcpPayload())) {
                result = packet.getSeqNumber();
                resultList.add(new Pair(result, packet));
                return resultList;
                /*if(packet.getTcpFlags().get("FIN")) {
                    return resultList;
                }
                else {
                    Pair separatePacket=checkForSeparateFINpacket(list,seq, ack, partnerB);
                    resultList.add(separatePacket);
                    return resultList;
                }*/

            }

        }
        return resultList;
    }

    public static Pair checkForThirdPacketFinishing(List<PcapPacket> list, Long seqB, InetAddress partnerA) {
        boolean result = false;

        for (PcapPacket packet : list) {
            if (packet.getIpSender().equals(partnerA) && packet.getTcpFlags().get("ACK")
                    && packet.getAckNumber() == (seqB + 1)
                    && !isHTTPContinuation(packet.getiPPayload(), packet.getTcpPayload())) {
                result = true;
                return new Pair(result, packet);
            }
        }
        return new Pair(result, null);
    }

    public static boolean isPSHOrACKFlagSet(HashMap<String, Boolean> flags,
                                             String ipPayload, String tcpPayload) {
        boolean result = false;

        if (flags.get("PSH") || flags.get("ACK")) {
            if (flags.get("FIN") == false && flags.get("SYN") == false) {
                result = true;
            }
            if (isHTTPContinuation(ipPayload, tcpPayload)) {
                result = true;
            }
        }
        return result;
    }

    private static boolean isHTTPContinuation(String iPPayload, String tcpPayload) {
        if (iPPayload != null && iPPayload.contains(HTTP_CONTINUATION_STRING) ||
                (tcpPayload != null && tcpPayload.contains(HTTP_CONTINUATION_STRING))) {
            return true;
        }
        return false;
    }

}

