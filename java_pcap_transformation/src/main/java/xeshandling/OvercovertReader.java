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

/**
 * The type Overcovert reader.
 * This class detects overcovert objects inside the packet-data and holds all corresponding algorithms.
 */
public class OvercovertReader {
    /**
     * Static String for checking a payload for HTTP-Continuation.
     */
    private static final String HTTP_CONTINUATION_STRING = "COMMAND:";

    /**
     * Gets overcovert.
     *
     * @param list     the list of PcapPackets to be analysed
     * @param team     the team-IP as java.net.InetAddress
     * @param teamMask the team mask as String in format f.i. 255.255.0.0
     * @param service  the service-IP as java.net.InetAddress
     * @return the list of found and created Overcovert-objects
     */
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
                int port;
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
                }
                else {
                    //Not finished-add packet
                    overcovert=checkWhichPacket(overcovert,current, team, teamMask, service);
                    if(isOvercovertFinished(overcovert)) {
                        overcovert=makeOvercovertFinished(overcovert);
                        result.add(overcovert);
                    }
                    currentlyOpen.put(port,overcovert);
                }
            }
            else {
                //New Overcovert-connection, create new instance.
                Integer portReceiver=current.getPortReceiver();
                Integer portSender=current.getPortSender();
                Integer port;
                Overcovert overcovert;
                if(current.getIpReceiver().equals(team)) {
                    overcovert=new Overcovert(portSender);
                    port=portSender;
                }
                else if(current.getIpSender().equals(team)){
                    overcovert=new Overcovert(portReceiver);
                    port=portReceiver;
                    overcovert.setServicePort(portSender);
                }
                else {
                    overcovert=new Overcovert(portSender);
                    port=portSender;
                    overcovert.setServicePort(portReceiver);
                }
                overcovert.setServiceIP(service);
                overcovert.setTeamIP(team);
                overcovert=checkWhichPacket(overcovert,current, team, teamMask, service);
                currentlyOpen.put(port,overcovert);
            }
        }
        return result;
    }

    /**
     * Method for setting the finished-boolean inside an Overcovert-object to true (and so to finish)
     * @param overcovert to be finished
     * @return the Overcovert-object which is now finished
     */
    private Overcovert makeOvercovertFinished(Overcovert overcovert) {
        overcovert.setFinished(true);
        return overcovert;
    }

    /**
     * Method for checking whether an Overcovert-object is finished or not
     * @param overcovert-instance to be checked
     * @return Boolean indicating if this Overcovert-instace is finished or not.
     */
    private boolean isOvercovertFinished(Overcovert overcovert) {
        HashMap<Handshakes, PcapPacket> handshakes= overcovert.getHandshakes();
        return handshakes.get(Handshakes.FIRST) != null && handshakes.get(Handshakes.SECOND) != null &&
                handshakes.get(Handshakes.THIRD) != null && overcovert.getReset() != null;
    }

    /**
     * Method for checking if all Finishes (First, Second and third) are exchanged and so the Overcovert-object
     * can be finished.
     * @param finish The HashMap of Key=Finishes (Enum indicating Finish-state) and Value=PcapPacket of the finish
     * @return Boolean indicating if all finish-packets were already exchanged and found.
     */
    private boolean isFinishFinished(HashMap<Finishes, PcapPacket> finish) {
        return finish.get(Finishes.FIRST) != null && finish.get(Finishes.SECOND) != null && finish.get(Finishes.THIRD) != null;
    }

    /**
     * Method for checking a packet about which part it covers (and so which data it holds) in the Overcovert-process.
     * @param overcovert Overcovert-instance which is to be set based on the informations
     * @param current Current PcapPacket to be checked
     * @param team team-IP as java.net.InetAddress
     * @param teamMask Subnet-mask of the team as String in form f.i. "255.255.255.0".
     * @param service service-IP as java.net.InetAddress
     * @return the Overcovert-instance, now expanded with new data, as the packet was processed.
     */
    private Overcovert checkWhichPacket(Overcovert overcovert, PcapPacket current,
                                        InetAddress team, String teamMask, InetAddress service) {

        HashMap<Handshakes, PcapPacket> handshakes= overcovert.getHandshakes();
        HashMap<Finishes, PcapPacket> finishes=overcovert.getLastfinishes();

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
            overcovert.setLastfinishes(finishes);
            return overcovert;
        }
        if (finishes.get(Finishes.FIRST) != null && finishes.get(Finishes.SECOND) == null
                && isSecondFinishPacket(current, finishes)) {
            finishes.put(Finishes.SECOND,current);
            overcovert.setLastfinishes(finishes);
            return overcovert;
        }
        if (finishes.get(Finishes.SECOND) != null && finishes.get(Finishes.THIRD) == null &&
                isThirdFinishPacket(current, finishes)) {
            finishes.put(Finishes.THIRD,current);
            overcovert.setLastfinishes(finishes);
            return overcovert;
        }
        if(finishes.get(Finishes.FIRST)==null && isFirstFinishPacket(current)) {
            handshakes.put(Handshakes.FIRST,current);
            overcovert.setHandshakes(handshakes);
            return overcovert;
        }
        if(isFinishFinished(finishes)) {
            List<HashMap<Finishes,PcapPacket>> finishList=overcovert.getFinishes();
            finishList.add(finishes);
            overcovert.setFinishes(finishList);
        }
        List<PcapPacket> inbetween=overcovert.getInbetween();
        inbetween.add(current);
        overcovert.setInbetween(inbetween);
        return overcovert;
    }

    /**
     * Method for checking if all handshakes are already found
     * @param handshakes HashMap of Key=Handshakes (Enum) and Key=PcapPacket in the current state
     * @return Boolean whether all three parts of the TCP-Three-Way-Handshakes are set or not.
     */
    private boolean isAlreadyHandshake(HashMap<Handshakes, PcapPacket> handshakes) {
        return handshakes.get(Handshakes.FIRST) != null
                && handshakes.get(Handshakes.SECOND) != null
                && handshakes.get(Handshakes.THIRD) != null;
    }

    /**
     * Method for checking if a Packet is a SYN-packet or not (SYN-Flag is set)
     * @param packet PcapPacket to be checked
     * @return Boolean indicating if it is a SYN-packet or not
     */
    private boolean isSYNpacket(PcapPacket packet) {
        return packet.getTcpFlags().get("SYN");
    }

    /**
     * Method for checking if a packet is a packet of the second-part of TCP-three-way-handshake
     * @param packet PcapPacket to be checked
     * @param service IP-address of the service as java.net.InetAddress
     * @return Boolean whether it is a second-part of Handshake packet or not.
     */
    private boolean isSecondPacketHandshake(PcapPacket packet, InetAddress service) {
        if (packet.getIpSender().equals(service) /*&& packet.getAckNumber() == (firstHandshake.getSeqNumber() + 1)*/) {
            return packet.getTcpFlags().get("SYN") && packet.getTcpFlags().get("ACK");
        }
        return false;
    }

    /**
     * Method for checking if a packet is a packet of the third-part of TCP-three-way-handshake
     * @param packet PcapPacket to be checked
     * @param client Client-IP as java.net.InetAddress
     * @param teamMask Subnet-mask of the client as String in format f.i. "255.255.255.0"
     * @param handshakes HashMap of Handshakes with Key=Handshake (Enum) and Value=PcapPacket
     * @return Boolean whether it is a third-part of Handshake packet or not.
     */
    private boolean isThirdPacketHandshake(PcapPacket packet, InetAddress client,
                                           String teamMask, HashMap<Handshakes, PcapPacket> handshakes) {
        PcapPacket secondHandshake= handshakes.get(Handshakes.SECOND);
        if (Network.isInSameNetwork(packet.getIpSender(), client, teamMask)
                && packet.getAckNumber() == (secondHandshake.getSeqNumber() + 1)
                && packet.getSeqNumber() == secondHandshake.getAckNumber()) {
            return packet.getTcpFlags().get("ACK");
        }
        return false;
    }

    /**
     * Method for checking whether a given PcapPacket is the beginning of a TCP-finish-Process or not
     * @param packet The PcapPacket to be checked
     * @return Boolean indicating if first finish-packet or not.
     */
    private boolean isFirstFinishPacket(PcapPacket packet) {
        return packet.getTcpFlags().get("FIN") && !isHTTPContinuation(packet.getIPPayload(), packet.getTcpPayload());
    }

    /**
     * Method for checking whether a given PcapPacket is the second part of a TCP-finish-Process or not
     * @param packet The PcapPacket to be checked
     * @param finishes The HashMap of Finishes with Key=Finishes (Enum) and Value=PcapPacket to this part of finishing
     * @return Boolean indicating if second finish-packet or not.
     */
    private boolean isSecondFinishPacket(PcapPacket packet, HashMap<Finishes, PcapPacket> finishes) {
        PcapPacket firstFinish= finishes.get(Finishes.FIRST);
        return packet.getIpSender().equals(firstFinish.getIpReceiver()) && packet.getTcpFlags().get("ACK")
                && packet.getAckNumber() == (firstFinish.getSeqNumber() + 1)
                && packet.getSeqNumber() == firstFinish.getAckNumber() && !isHTTPContinuation(packet.getIPPayload(), packet.getTcpPayload());
    }

    /**
     * Method for checking whether a given PcapPacket is the third part of a TCP-finish-Process or not
     * @param packet The PcapPacket to be checked
     * @param finishes The HashMap of Finishes with Key=Finishes (Enum) and Value=PcapPacket to this part of finishing
     * @return Boolean indicating if third finish-packet or not.
     */
    private boolean isThirdFinishPacket(PcapPacket packet, HashMap<Finishes, PcapPacket> finishes) {
        return packet.getIpSender().equals(finishes.get(Finishes.FIRST).getIpSender())
                && packet.getTcpFlags().get("ACK")
                && packet.getAckNumber() == (finishes.get(Finishes.SECOND).getSeqNumber() + 1)
                && !isHTTPContinuation(packet.getIPPayload(), packet.getTcpPayload());
    }

    /**
     * Method for checking if a given payload contains HTTP-continuation
     * @param iPPayload Payload of the IP-packet as String
     * @param tcpPayload Payload of the TCP-packet as String
     * @return Boolean indicating if payload contains HTTP-continuation
     */
    private static boolean isHTTPContinuation(String iPPayload, String tcpPayload) {
        return (iPPayload != null && iPPayload.contains(HTTP_CONTINUATION_STRING)) ||
                (tcpPayload != null && tcpPayload.contains(HTTP_CONTINUATION_STRING));
    }
}
