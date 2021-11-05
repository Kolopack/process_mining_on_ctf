package xeshandling;

import enumerations.MostwantedPart;
import exceptions.TimestampsNotFittingException;
import javafx.util.Pair;
import packets.Mostwanted;
import packets.PcapPacket;
import packets.Session;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class DefaultEventCreator {
    private static Logger logger = Logger.getLogger(DefaultEventCreator.class.getName());

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

    public static Pair checkForThirdPacketThreeWayHandshake(List<PcapPacket> list, int index, Long seqSecond, Long ackFirst, InetAddress client) {


        for (int i = index + 1; i < list.size(); ++i) {
            PcapPacket packet = list.get(i);
            if (packet.getIpSender().equals(client) && packet.getAckNumber() == (seqSecond + 1)
                    && packet.getSeqNumber() == ackFirst) {
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

            if (current.getTcpFlags().get("FIN")) {
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
                    && packet.getAckNumber() == (seq + 1) && packet.getSeqNumber() == ack) {
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

    /*private static Pair checkForSeparateFINpacket(List<PcapPacket> list, Long seq, Long ack, InetAddress partnerB) {
        Long result=null;

        //Second case: ACK and FIN are not sent in the same packet
        for(PcapPacket packet: list) {
            if( packet.getIpSender().equals(partnerB) && packet.getTcpFlags().get("ACK")
                    && packet.getAckNumber()==(seq+1) && !packet.getTcpFlags().get("FIN")
                    && packet.getSeqNumber()==ack) {
                    result=packet.getSeqNumber();
                    return new Pair(result,packet);
            }
        }
        return new Pair(result,null);
    }*/

    public static Pair checkForThirdPacketFinishing(List<PcapPacket> list, Long seqB, InetAddress partnerA) {
        boolean result = false;

        for (PcapPacket packet : list) {
            if (packet.getIpSender().equals(partnerA) && packet.getTcpFlags().get("ACK")
                    && packet.getAckNumber() == (seqB + 1)) {
                result = true;
                return new Pair(result, packet);
            }
        }
        return new Pair(result, null);
    }

    public static List<Mostwanted> getPSHACKSessionsBetween(List<Session> handshakes, List<List<PcapPacket>> finishes, List<PcapPacket> allPackets,
                                                            InetAddress team, InetAddress service) {
        List<Mostwanted> result = new ArrayList<>();
        List<PcapPacket> alreadyStored = new ArrayList<>();

        for (Session session : handshakes) {
            alreadyStored.addAll(session.getPackets());
        }

        for (List<PcapPacket> finish : finishes) {
            alreadyStored.addAll(finish);
        }

        for (int i = 0; i < handshakes.size() && i < finishes.size(); ++i) {
            Mostwanted mostwanted = new Mostwanted(team, service);
            HashMap<MostwantedPart, List<PcapPacket>> mostwantedPackets = new HashMap<>();

            List<PcapPacket> handshakePackets = handshakes.get(i).getPackets();
            Integer indexFirstPacketHandshake = handshakes.get(i).getCertainIndex();
            PcapPacket lastPacketHandshake = handshakePackets.get(handshakePackets.size() - 1);
            Timestamp timestampHandshake = lastPacketHandshake.getArrivalTime();

            List<PcapPacket> finishesPackets = finishes.get(i);
            PcapPacket firstPacketFinishes = finishes.get(i).get(0);
            Timestamp timestampFinish = firstPacketFinishes.getArrivalTime();

            int ausweichIndex=i+1;
            while (timestampHandshake.after(timestampFinish) && ausweichIndex<finishes.size()) {
                System.out.println("Had to take the next one sadly.");
                finishesPackets=finishes.get(ausweichIndex);
                firstPacketFinishes = finishes.get(i).get(0);
                timestampFinish = firstPacketFinishes.getArrivalTime();
                ++ausweichIndex;
            }

            if (timestampHandshake.after(timestampFinish)) {
                System.out.println("Oh shit, this loop was not enough");
                System.out.println("Handshake:");
                for (PcapPacket packet : handshakePackets) {
                    System.out.println(packet);
                }
                System.out.println("Finish:");
                for (PcapPacket packet : finishesPackets) {
                    System.out.println(packet);
                }
                throw new TimestampsNotFittingException();
            } else {
                List<PcapPacket> pshAttacks = new ArrayList<>();

                for (int j = indexFirstPacketHandshake; j < allPackets.size(); ++j) {
                    PcapPacket current = allPackets.get(j);
                    if (!(current.getArrivalTime().before(timestampFinish))) {
                        break;
                    }
                    if (isPSHOrACKFlagSet(current.getTcpFlags()) && !alreadyStored.contains(current)) {
                        pshAttacks.add(current);
                        alreadyStored.add(current);
                    }
                }

                if (!pshAttacks.isEmpty()) {
                    mostwantedPackets.put(MostwantedPart.HANDSHAKE, handshakePackets);
                    mostwantedPackets.put(MostwantedPart.PSHACK, pshAttacks);
                    mostwantedPackets.put(MostwantedPart.FINISHING, finishesPackets);

                    mostwanted.setPackets(mostwantedPackets);
                    result.add(mostwanted);
                }
            }
        }
        return result;
    }

    private static boolean isPSHOrACKFlagSet(HashMap<String, Boolean> flags) {
        boolean result = false;

        if (flags.get("PSH") || flags.get("ACK")) {
            if (flags.get("FIN") == false && flags.get("SYN") == false) {
                result = true;
            }
        }
        return result;
    }

}

