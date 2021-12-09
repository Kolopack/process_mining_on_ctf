package xeshandling;

import enumerations.MostwantedPart;
import packets.PcapPacket;
import packets.Session;
import serviceRepresentation.Flagsubmission;
import serviceRepresentation.Mostwanted;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FlagsubmissionEventCreator {

    public static List<Flagsubmission> buildFlagsubmissions(List<Session> handshakes, List<List<PcapPacket>> finishes,
                                                           List<PcapPacket> packetList, InetAddress teamIP, InetAddress serviceIP) {
        List<Flagsubmission> result=new ArrayList<>();

        for (int i = 0; i < handshakes.size() && i < finishes.size(); ++i) {
            Flagsubmission flagsubmission=new Flagsubmission(teamIP,serviceIP);

            List<PcapPacket> handshakePackets = handshakes.get(i).getPackets();
            Integer indexFirstPacketHandshake = handshakes.get(i).getCertainIndex();

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
}
