package xeshandling;

import creation.AbstractXESService;
import creation.IService;
import enumerations.MostwantedPart;
import packets.Mostwanted;
import packets.PcapPacket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MostwantedReader {
    private static final String HTTP_CONTINUATION_STRING = "COMMAND:";

    PcapPacket firstHandshake = null;
    PcapPacket secondHandshake = null;
    PcapPacket thirdHandshake = null;

    List<PcapPacket> inbetween = new ArrayList<>();

    PcapPacket firstFinish = null;
    PcapPacket secondFinish = null;
    PcapPacket thirdFinish = null;

    InetAddress client = null;
    InetAddress server = null;


    public List<Mostwanted> getMostwanteds(List<PcapPacket> list, InetAddress team, InetAddress service) {

        List<Mostwanted> result = new ArrayList<>();


        for (int i = 0; i < list.size(); ++i) {
            System.out.println("Current i: " + i);
            PcapPacket current = list.get(i);

            if (isMostwantedFinished()) {
                Mostwanted mostwanted = buildMostwanted(team, service);
                result.add(mostwanted);
                resetInstanceVariables();
            }

            if (firstHandshake == null && isSYNpacket(current)) {
                firstHandshake = current;
                client = current.getIpSender();
                server = current.getIpReceiver();
                continue;
            }
            if (firstHandshake != null && secondHandshake == null
                    && isSecondPacketHandshake(current, server)) {
                secondHandshake = current;
                continue;
            }
            if (secondHandshake != null && thirdHandshake == null &&
                    isThirdPacketHandshake(current, client)) {
                thirdHandshake = current;
                continue;
            }

            if (firstFinish == null && isFirstFinishPacket(current) && isAlreadyHandshake()) {
                firstFinish = current;
                continue;
            }
            if (firstFinish != null && secondFinish == null && isSecondFinishPacket(current)) {
                secondFinish = current;
                continue;
            }
            if (secondFinish != null && thirdFinish == null && isThirdFinishPacket(current)) {
                thirdFinish = current;
                continue;
            }
            if (firstHandshake != null && secondHandshake != null && thirdHandshake != null &&
                    isPSHOrACKFlagSet(current.getTcpFlags(), current.getiPPayload(), current.getTcpPayload())
            ) {
                inbetween.add(current);
                continue;
            }
            System.out.println("Nothing out of Mostwanted - ignored.");
        }
        return result;
    }

    private boolean isMostwantedFinished() {
        if (firstHandshake != null && secondHandshake != null && thirdHandshake != null
                && firstFinish != null && secondFinish != null) {
            return true;
        }
        return false;
    }

    private Mostwanted buildMostwanted(InetAddress team, InetAddress service) {
        Mostwanted mostwanted = new Mostwanted(team, service);

        HashMap<MostwantedPart, List<PcapPacket>> packets = new HashMap<>();

        List<PcapPacket> handshakes = new ArrayList<>();
        handshakes.add(firstHandshake);
        handshakes.add(secondHandshake);
        handshakes.add(thirdHandshake);
        packets.put(MostwantedPart.HANDSHAKE, handshakes);

        packets.put(MostwantedPart.PSHACK, inbetween);

        List<PcapPacket> finishes = new ArrayList<>();
        finishes.add(firstFinish);
        finishes.add(secondFinish);
        if (thirdFinish != null) {
            finishes.add(thirdFinish);
        }
        packets.put(MostwantedPart.FINISHING, finishes);
        mostwanted.setPackets(packets);

        return mostwanted;
    }

    private void resetInstanceVariables() {
        firstHandshake = null;
        secondHandshake = null;
        thirdHandshake = null;
        inbetween = new ArrayList<>();
        firstFinish = null;
        secondFinish = null;
        thirdFinish = null;
        client = null;
        server = null;
    }

    private boolean isAlreadyHandshake() {
        if (firstHandshake == null || secondHandshake == null || thirdHandshake == null) {
            return false;
        }
        return true;
    }

    private boolean isSYNpacket(PcapPacket packet) {
        if (packet.getTcpFlags().get("SYN")) {
            return true;
        }
        return false;
    }

    private boolean isSecondPacketHandshake(PcapPacket packet, InetAddress server) {
        if (packet.getIpSender().equals(server) && packet.getAckNumber() == (firstHandshake.getSeqNumber() + 1)) {
            if (packet.getTcpFlags().get("SYN") && packet.getTcpFlags().get("ACK")) {
                return true;
            }
        }
        return false;
    }

    private boolean isThirdPacketHandshake(PcapPacket packet, InetAddress client) {
        if (packet.getIpSender().equals(client) && packet.getAckNumber() == (secondHandshake.getSeqNumber() + 1)
                && packet.getSeqNumber() == secondHandshake.getAckNumber()) {
            if (packet.getTcpFlags().get("ACK")) {
                return true;
            }
        }
        return false;
    }

    private boolean isFirstFinishPacket(PcapPacket packet) {
        if (packet.getTcpFlags().get("FIN") && !isHTTPContinuation(packet.getiPPayload(),packet.getTcpPayload())) {
            return true;
        }
        return false;
    }

    private boolean isSecondFinishPacket(PcapPacket packet) {
        if (packet.getIpSender().equals(firstFinish.getIpReceiver()) && packet.getTcpFlags().get("ACK")
                && packet.getAckNumber() == (firstFinish.getSeqNumber() + 1)
                && packet.getSeqNumber() == firstFinish.getAckNumber() && !isHTTPContinuation(packet.getiPPayload(), packet.getTcpPayload())) {
            return true;
        }
        return false;
    }

    private boolean isThirdFinishPacket(PcapPacket packet) {
        if (packet.getIpSender().equals(firstFinish.getIpSender()) && packet.getTcpFlags().get("ACK")
                && packet.getAckNumber() == (secondFinish.getSeqNumber() + 1)
                && !isHTTPContinuation(packet.getiPPayload(), packet.getTcpPayload())) {
            return true;
        }
        return false;
    }

    private static boolean isPSHOrACKFlagSet(HashMap<String, Boolean> flags, String iPPayload, String tcpPayload) {
        boolean result = false;

        if (flags.get("PSH") || flags.get("ACK")) {
            if (flags.get("FIN") == false && flags.get("SYN") == false) {
                result = true;
            }
            if(isHTTPContinuation(iPPayload,tcpPayload)) {
                result=true;
            }

        }
        return result;
    }

    private static boolean isHTTPContinuation(String iPPayload, String tcpPayload) {
        if (iPPayload!=null && iPPayload.contains(HTTP_CONTINUATION_STRING) ||
                (tcpPayload!=null && tcpPayload.contains(HTTP_CONTINUATION_STRING))) {
            return true;
        }
        return false;
    }
}
