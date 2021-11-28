package xeshandling;

import enumerations.Finishes;
import enumerations.Handshakes;
import packets.PcapPacket;
import scanning.Network;
import serviceRepresentation.Overcovert;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class OvercovertReader {
    private static final Logger logger= Logger.getLogger(OvercovertReader.class.getName());

    private static final String HTTP_CONTINUATION_STRING = "COMMAND:";

    public List<Overcovert> getOvercovert(List<PcapPacket> list, InetAddress team, String teamMask, InetAddress service) {

        List<Overcovert> result = new ArrayList<>();
        HashMap<Integer, Overcovert> currentlyOpen=new HashMap<>();

        for (int i = 0; i < list.size(); ++i) {
            System.out.println("Current i: " + i);

            PcapPacket current = list.get(i);

            //Is Port already known?
            if(currentlyOpen.containsKey(current.getPortSender()) || currentlyOpen.containsKey(current.getPortReceiver())) {
                //Port is already known
                Overcovert overcovert;
                Integer port;
                if(currentlyOpen.get(current.getPortSender())!=null) {
                    overcovert=currentlyOpen.get(current.getPortSender());
                    port=current.getPortSender();
                }
                else {
                    overcovert=currentlyOpen.get(current.getPortReceiver());
                    port=current.getPortReceiver();
                }

                //Is Session still open?
                if(overcovert.isFinished()) {
                    //No, its already finished
                    result.add(overcovert);
                    Overcovert newOvercovert=new Overcovert(port);
                    newOvercovert=checkWhichPacket(newOvercovert,current, team, teamMask, service);
                    currentlyOpen.put(port,newOvercovert);
                    continue;
                }
                else {
                    //Not finished-add packet
                    overcovert=checkWhichPacket(overcovert,current, team, teamMask, service);
                    currentlyOpen.put(port,overcovert);
                    continue;
                }
            }
            else {
                //New Overcovert-connection, create new instance
                Integer portA=current.getPortReceiver();
                Integer portB=current.getPortSender();
                Integer port;
                Overcovert overcovert;
                if(portA.equals(team)) {
                    overcovert=new Overcovert(portB);
                    port=portB;
                }
                else {
                    overcovert=new Overcovert(portA);
                    port=portA;
                }
                overcovert=checkWhichPacket(overcovert,current, team, teamMask, service);
                currentlyOpen.put(port,overcovert);
            }
        }
        return result;
    }

    private boolean isOvercovertFinished(Overcovert overcovert) {
        HashMap<Handshakes, PcapPacket> handshakes= overcovert.getHandshakes();
        if (handshakes.get(Handshakes.FIRST) != null && handshakes.get(Handshakes.SECOND) != null &&
                handshakes.get(Handshakes.THIRD) != null && overcovert.getReset() != null) {
            return true;
        }
        return false;
    }

    private Overcovert checkWhichPacket(Overcovert overcovert, PcapPacket current,
                                        InetAddress team, String teamMask, InetAddress service) {

        HashMap<Handshakes, PcapPacket> handshakes= overcovert.getHandshakes();
        HashMap<Finishes, PcapPacket> finishes=overcovert.getFinishes();

        if (handshakes.get(Handshakes.FIRST) == null && isSYNpacket(current)) {
            handshakes.put(Handshakes.FIRST,current);
            overcovert.setHandshakes(handshakes);
            return overcovert;
        }
        if (handshakes.get(Handshakes.FIRST) != null && handshakes.get(Handshakes.SECOND) == null
                && isSecondPacketHandshake(current, service)) {
            handshakes.put(Handshakes.SECOND,current);
            overcovert.setHandshakes(handshakes);
            return overcovert;
        }
        if (handshakes.get(Handshakes.SECOND) != null && handshakes.get(Handshakes.THIRD) == null &&
                isThirdPacketHandshake(current, team,teamMask, handshakes)) {
            handshakes.put(Handshakes.THIRD,current);
            overcovert.setHandshakes(handshakes);
            return overcovert;
        }
        if(overcovert.getReset()==null && OvercovertEventCreator.isFullReset(current)) {
            overcovert.setReset(current);
            return overcovert;
        }
        if (finishes.get(Finishes.FIRST) == null && isFirstFinishPacket(current) && isAlreadyHandshake(handshakes)) {
            finishes.put(Finishes.FIRST,current);
            overcovert.setFinishes(finishes);
            return overcovert;
        }
        if (finishes.get(Finishes.FIRST) != null && finishes.get(Finishes.SECOND) == null
                && isSecondFinishPacket(current, finishes)) {
            finishes.put(Finishes.SECOND,current);
            overcovert.setFinishes(finishes);
            return overcovert;
        }
        if (finishes.get(Finishes.SECOND) != null && finishes.get(Finishes.THIRD) == null &&
                isThirdFinishPacket(current, finishes)) {
            finishes.put(Finishes.THIRD,current);
            overcovert.setFinishes(finishes);
            return overcovert;
        }
        if(finishes.get(Finishes.FIRST)==null && isFirstFinishPacket(current)) {
            handshakes.put(Handshakes.FIRST,current);
            overcovert.setHandshakes(handshakes);
            return overcovert;
        }
        /*if (handshakes.get(Handshakes.FIRST) != null && handshakes.get(Handshakes.SECOND) != null
                && handshakes.get(Handshakes.THIRD) != null &&
                isPSHOrACKFlagSet(current.getTcpFlags(), current.getiPPayload(), current.getTcpPayload())
        ) {
            List<PcapPacket> inbetween=overcovert.getInbetween();
            inbetween.add(current);
            overcovert.setInbetween(inbetween);
            return overcovert;
        }*/
        List<PcapPacket> inbetween=overcovert.getInbetween();
        inbetween.add(current);
        overcovert.setInbetween(inbetween);
        //logger.warning("Packet could not be assigned");
        return overcovert;
    }

    private boolean isAlreadyHandshake(HashMap<Handshakes, PcapPacket> handshakes) {
        if (handshakes.get(Handshakes.FIRST) == null
                || handshakes.get(Handshakes.SECOND) == null
                || handshakes.get(Handshakes.THIRD) == null) {
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

    private boolean isSecondPacketHandshake(PcapPacket packet, InetAddress service) {
        if (packet.getIpSender().equals(service) /*&& packet.getAckNumber() == (firstHandshake.getSeqNumber() + 1)*/) {
            if (packet.getTcpFlags().get("SYN") && packet.getTcpFlags().get("ACK")) {
                return true;
            }
        }
        return false;
    }

    private boolean isThirdPacketHandshake(PcapPacket packet, InetAddress client,
                                           String teamMask, HashMap<Handshakes, PcapPacket> handshakes) {
        PcapPacket secondHandshake= handshakes.get(Handshakes.SECOND);
        if (Network.isInSameNetwork(packet.getIpSender(), client, teamMask)
                && packet.getAckNumber() == (secondHandshake.getSeqNumber() + 1)
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

    private boolean isSecondFinishPacket(PcapPacket packet, HashMap<Finishes, PcapPacket> finishes) {
        PcapPacket firstFinish= finishes.get(Finishes.FIRST);
        if (packet.getIpSender().equals(firstFinish.getIpReceiver()) && packet.getTcpFlags().get("ACK")
                && packet.getAckNumber() == (firstFinish.getSeqNumber() + 1)
                && packet.getSeqNumber() == firstFinish.getAckNumber() && !isHTTPContinuation(packet.getiPPayload(), packet.getTcpPayload())) {
            return true;
        }
        return false;
    }

    private boolean isThirdFinishPacket(PcapPacket packet, HashMap<Finishes, PcapPacket> finishes) {
        if (packet.getIpSender().equals(finishes.get(Finishes.FIRST).getIpSender())
                && packet.getTcpFlags().get("ACK")
                && packet.getAckNumber() == (finishes.get(Finishes.SECOND).getSeqNumber() + 1)
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
