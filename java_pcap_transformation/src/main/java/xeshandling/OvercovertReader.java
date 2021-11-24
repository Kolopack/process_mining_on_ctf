package xeshandling;

import enumerations.Finishes;
import enumerations.Handshakes;
import enumerations.OvercovertPart;
import packets.PcapPacket;
import scanning.Network;
import serviceRepresentation.Overcovert;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OvercovertReader {
    private static final String HTTP_CONTINUATION_STRING = "COMMAND:";

    public List<Overcovert> getOvercovert(List<PcapPacket> list, InetAddress team, InetAddress service) {

        List<Overcovert> result = new ArrayList<>();
        HashMap<Integer, Overcovert> currentlyOpen=new HashMap<>();

        for (int i = 0; i < list.size(); ++i) {
            System.out.println("Current i: " + i);

            PcapPacket current = list.get(i);

            if(currentlyOpen.containsKey(current.getPortSender()) || currentlyOpen.containsKey(current.getPortReceiver())) {
                //Session is already known
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

                HashMap<Handshakes, PcapPacket> handshakes= overcovert.getHandshakes();
                HashMap<Finishes, PcapPacket> finishes=overcovert.getFinishes();

                if (handshakes.get(Handshakes.FIRST) == null && isSYNpacket(current)) {
                    handshakes.put(Handshakes.FIRST,current);
                    overcovert.setHandshakes(handshakes);
                    currentlyOpen.put(current.getPortSender(),overcovert);
                    continue;
                }
                if (handshakes.get(Handshakes.FIRST) != null && handshakes.get(Handshakes.SECOND) == null
                        && isSecondPacketHandshake(current, service)) {
                    handshakes.put(Handshakes.SECOND,current);
                    overcovert.setHandshakes(handshakes);
                    continue;
                }
                if (handshakes.get(Handshakes.SECOND) != null && handshakes.get(Handshakes.THIRD) == null &&
                        isThirdPacketHandshake(current, team)) {
                    handshakes.put(Handshakes.THIRD,current);
                    overcovert.setHandshakes(handshakes);
                    continue;
                }
                if(overcovert.getReset()==null && OvercovertEventCreator.isFullReset(current)) {
                    overcovert.setReset(current);
                    continue;
                }
                if (finishes.get(Finishes.FIRST) == null && isFirstFinishPacket(current) && isAlreadyHandshake()) {
                    finishes.put(Finishes.FIRST,current);
                    overcovert.setFinishes(finishes);
                    continue;
                }
                if (finishes.get(Finishes.FIRST) != null && finishes.get(Finishes.SECOND) == null && isSecondFinishPacket(current)) {
                    finishes.put(Finishes.SECOND,current);
                    overcovert.setFinishes(finishes);
                    continue;
                }
                if (finishes.get(Finishes.SECOND) != null && finishes.get(Finishes.THIRD) == null && isThirdFinishPacket(current)) {
                    finishes.put(Finishes.THIRD,current);
                    overcovert.setFinishes(finishes);
                    continue;
                }
                if(finishes.get(Finishes.FIRST)==null && isFirstFinishPacket(current)) {
                    handshakes.put(Handshakes.FIRST,current);
                    overcovert.setHandshakes(handshakes);
                    continue;
                }
                if (handshakes.get(Handshakes.FIRST) != null && handshakes.get(Handshakes.SECOND) != null
                        && handshakes.get(Handshakes.THIRD) != null &&
                        isPSHOrACKFlagSet(current.getTcpFlags(), current.getiPPayload(), current.getTcpPayload())
                ) {
                    List<PcapPacket> inbetween=overcovert.getInbetween();
                    inbetween.add(current);
                    continue;
                }

                if (isOvercovertFinished(overcovert)) {
                    result.add(overcovert);
                    currentlyOpen.remove(port);
                    //TODO: check which packet
                }
                //Wenn schon voll -> abschließen
                if(overcovert.getReset()!=null && overcovert.getHandshakes().get(Handshakes.FIRST)!=null) {
                    result.add(overcovert);
                    currentlyOpen.remove(port);
                    resetInstanceVariables();
                    continue;
                }
                if(firstHandshake!=null && isSYNpacket(current)) {
                    Overcovert overcovert = buildOvercovert(team, service);
                    result.add(overcovert);
                    resetInstanceVariables();
                    firstHandshake=current;
                    client = current.getIpSender();
                    server = current.getIpReceiver();
                    continue;
                }
                System.out.println("Nothing out of Mostwanted - ignored.");
            }
            else {
                //New Overcovert-connection, create new instance
                Integer portA=current.getPortReceiver();
                Integer portB=current.getPortSender();
                Overcovert overcovert;
                if(portA.equals(team)) {
                    overcovert=new Overcovert(portA);
                }
                else {
                    overcovert=new Overcovert(portB);
                }
                currentlyOpen.put(current.getPortSender(),overcovert);
                //TODO: checken was für ein Paket es ist
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

    private Overcovert buildOvercovert(InetAddress team, InetAddress service) {
        Overcovert overcovert = new Overcovert(team, service);

        HashMap<Handshakes,PcapPacket> handshakes = new HashMap<>();
        if(firstHandshake!=null) {
            handshakes.put(Handshakes.FIRST,firstHandshake);
        }
        if(secondHandshake!=null) {
            handshakes.put(Handshakes.SECOND, secondHandshake);
        }
        if(thirdHandshake!=null) {
            handshakes.put(Handshakes.THIRD, thirdHandshake);
        }
        overcovert.setHandshakes(handshakes);

        HashMap<OvercovertPart, List<PcapPacket>> packets = new HashMap<>();
        packets.put(OvercovertPart.ELSE, inbetween);

        List<PcapPacket> finishes = new ArrayList<>();
        finishes.add(firstFinish);
        finishes.add(secondFinish);
        if (thirdFinish != null) {
            finishes.add(thirdFinish);
        }
        packets.put(OvercovertPart.FINISHING, finishes);

        overcovert.setInbetween(packets);

        return overcovert;
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

    private boolean isSecondPacketHandshake(PcapPacket packet, InetAddress service) {
        if (packet.getIpSender().equals(server) /*&& packet.getAckNumber() == (firstHandshake.getSeqNumber() + 1)*/) {
            if (packet.getTcpFlags().get("SYN") && packet.getTcpFlags().get("ACK")) {
                return true;
            }
        }
        return false;
    }

    private boolean isThirdPacketHandshake(PcapPacket packet, InetAddress client) {
        if (Network.isInSameNetwork(packet.getIpSender(), client, ) && packet.getAckNumber() == (secondHandshake.getSeqNumber() + 1)
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
