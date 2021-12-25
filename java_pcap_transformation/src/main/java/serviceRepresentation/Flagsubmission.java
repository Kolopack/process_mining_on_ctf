package serviceRepresentation;

import packets.PcapPacket;

import java.util.List;

/**
 * The type Flagsubmission.
 * User for representing one flagsubmission-trace
 */
public class Flagsubmission {
    /**
     * List of PcapPackets responsible for the handshake
     */
    private List<PcapPacket> handshakes;
    /**
     * List of PcapPackets responsible for the finishing of the connection
     */
    private List<PcapPacket> finishes;
    /**
     * List of PcapPackets between (so for the real task)
     */
    private List<PcapPacket> packetsInbetween;

    /**
     * Instantiates a new Flagsubmission.
     */
    public Flagsubmission() {
    }

    /**
     * Gets handshakes.
     *
     * @return the handshakes
     */
    public List<PcapPacket> getHandshakes() {
        return handshakes;
    }

    /**
     * Sets handshakes.
     *
     * @param handshakes the handshakes
     */
    public void setHandshakes(List<PcapPacket> handshakes) {
        this.handshakes = handshakes;
    }

    /**
     * Gets finishes.
     *
     * @return the finishes
     */
    public List<PcapPacket> getFinishes() {
        return finishes;
    }

    /**
     * Sets finishes.
     *
     * @param finishes the finishes
     */
    public void setFinishes(List<PcapPacket> finishes) {
        this.finishes = finishes;
    }

    /**
     * Gets packets inbetween.
     *
     * @return the packets inbetween
     */
    public List<PcapPacket> getPacketsInbetween() {
        return packetsInbetween;
    }

    /**
     * Sets packets inbetween.
     *
     * @param packetsInbetween the packets inbetween
     */
    public void setPacketsInbetween(List<PcapPacket> packetsInbetween) {
        this.packetsInbetween = packetsInbetween;
    }
}
