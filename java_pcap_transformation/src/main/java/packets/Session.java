package packets;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Session.
 * This class is used when a List of Packets, but also an index (for instance of the first found packet)
 * is required.
 * For instance this class was used for finding handshakes in the Mostwanted-algorithm
 */
public class Session {
    /**
     * Integer for storing a specific index which is important in a context.
     * (For instance used as the index of the first packet of a TCP-handshake, but also suited for other usage).
     */
    private Integer certainIndex;
    /**
     * List of PcapPackets of this Session
     */
    private List<PcapPacket> packets;

    /**
     * Instantiates a new Session.
     */
    public Session() {
        certainIndex =null;
        packets=new ArrayList<>();
    }

    /**
     * Gets certain index.
     *
     * @return the certain index
     */
    public int getCertainIndex() {
        return certainIndex;
    }

    /**
     * Gets packets.
     *
     * @return the packets
     */
    public List<PcapPacket> getPackets() {
        return packets;
    }

    /**
     * Sets certain index.
     *
     * @param firstPacket the first packet
     */
    public void setCertainIndex(int firstPacket) {
        this.certainIndex = firstPacket;
    }

    /**
     * Sets packets.
     *
     * @param packets the packets
     */
    public void setPackets(List<PcapPacket> packets) {
        this.packets = packets;
    }

    @Override
    public String toString() {
        StringBuilder print= new StringBuilder("Session{" +
                "indexFirstPacket=" + certainIndex + "\n");
                print.append("The packets involved:\n");
                for(PcapPacket  packet : packets) {
                    print.append(packet).append("\n");
                }
                print.append("};");
        return print.toString();
    }
}
