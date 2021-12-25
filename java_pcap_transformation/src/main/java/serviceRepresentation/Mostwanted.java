package serviceRepresentation;

import enumerations.MostwantedPart;
import packets.PcapPacket;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Mostwanted.
 * Used for representing a Mostwanted-trace and all required data inside of the implementation.
 */
public class Mostwanted {
    /**
     * IP-address of the team as java.net.InetAddress
     */
    private final InetAddress team;
    /**
     * IP-address of the service as java.net.InetAddress
     */
    private final InetAddress service;
    /**
     * HashMap which contains all the PcapPackets contained in this Mostwanted-trace.
     * Key: Part of Mostwanted-process
     * Value: List of PcapPacket which belongs to each part
     */
    private HashMap<MostwantedPart,List<PcapPacket>> packets;
    /**
     * Boolean indicating if the Mostwanted-process was successful or not
     */
    private boolean success;
    /**
     * The flag achieved as a String
     */
    private String flag;

    /**
     * Instantiates a new Mostwanted.
     *
     * @param team    the team
     * @param service the service
     */
    public Mostwanted(InetAddress team, InetAddress service) {
        this.team=team;
        this.service=service;
    }

    /**
     * Sets success.
     *
     * @param success the success
     * @param flag    the flag
     */
    public void setSuccess(boolean success, String flag) {
        this.success=success;
        this.flag=flag;
    }

    /**
     * Gets three way handshake packets.
     *
     * @return the three way handshake packets
     */
    public List<PcapPacket> getThreeWayHandshakePackets() {
        return packets.get(MostwantedPart.HANDSHAKE);
    }

    /**
     * Gets tcp finishing packets.
     *
     * @return the tcp finishing packets
     */
    public List<PcapPacket> getTCPFinishingPackets() {
        return packets.get(MostwantedPart.FINISHING);
    }

    /**
     * Gets pshack attacks.
     *
     * @return the pshack attacks
     */
    public List<PcapPacket> getPSHACKAttacks() {
        return packets.get(MostwantedPart.PSHACK);
    }

    /**
     * Was successful boolean, for checking ether this Mostwanted-trace was successful or not
     *
     * @return the boolean
     */
    public boolean wasSuccessful() {
        return success;
    }

    /**
     * Gets full amount of packets.
     *
     * @return the full amount of packets
     */
    public int getFullAmountOfPackets() {
        int result=0;
        for(Map.Entry<MostwantedPart, List<PcapPacket>> elem : packets.entrySet()) {
            result=result+elem.getValue().size();
        }
        return result;
    }

    /**
     * Sets packets.
     *
     * @param packets the packets
     */
    public void setPackets(HashMap<MostwantedPart, List<PcapPacket>> packets) {
        this.packets = packets;
    }

    @Override
    public String toString() {
        StringBuilder result= new StringBuilder();
        result.append("Mostwanted{" + "team=").append(team).append(", service=").append(service).append(", success=").append(success).append(", flag='").append(flag).append('\'').append(" , amount of packets=").append(getFullAmountOfPackets()).append("\n");
                result.append("{").append(MostwantedPart.HANDSHAKE.name()).append(":\n");
                List<PcapPacket> handshake=packets.get(MostwantedPart.HANDSHAKE);
                for(PcapPacket packet : handshake) {
                    result.append(packet.toString()).append("\n");
                }
                result.append("}\n");

                result.append("{").append(MostwantedPart.PSHACK.name()).append(":\n");
                List<PcapPacket> pshack=packets.get(MostwantedPart.PSHACK);
                for(PcapPacket packet : pshack) {
                    result.append(packet.toString()).append("\n");
                }
                result.append("}\n");

                result.append("{").append(MostwantedPart.FINISHING.name()).append(":\n");
                List<PcapPacket> finish=packets.get(MostwantedPart.FINISHING);
                for(PcapPacket packet : finish) {
                    result.append(packet.toString()).append("\n");
                }

                result.append("};");
                return result.toString();
    }
}
