package xeshandling;

import enumerations.MostwantedPart;
import serviceRepresentation.Mostwanted;
import packets.PcapPacket;
import packets.Session;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MostwantedEventCreator {

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

            List<PcapPacket> finishesPackets = finishes.get(i);
            PcapPacket firstPacketFinishes = finishes.get(i).get(0);
            Timestamp timestampFinish = firstPacketFinishes.getArrivalTime();

            List<PcapPacket> pshAttacks = new ArrayList<>();

            for (int j = indexFirstPacketHandshake; j < allPackets.size(); ++j) {
                PcapPacket current = allPackets.get(j);
                if (!(current.getArrivalTime().before(timestampFinish))) {
                    break;
                }
                if (DefaultEventCreator.isPSHOrACKFlagSet(current.getTcpFlags(), current.getiPPayload(), current.getTcpPayload())
                        && !alreadyStored.contains(current)) {
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
        return result;
    }
}
