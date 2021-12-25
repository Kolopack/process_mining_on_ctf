package xeshandling;

import packets.PcapPacket;

/**
 * The type Overcovert event creator.
 * Contains method which are relevant for creating Elements specific to the Overcovert-service
 */
public class OvercovertEventCreator {

    /**
     * Is full reset boolean.
     *Checks if a given Packet is a RST and ACK packet (Reset and Acknowledge in one packet)
     * @param packet the PcapPacket to be checked
     * @return the boolean if it is a RST and ACK-packet or not
     */
    public static boolean isFullReset(PcapPacket packet) {
        return packet.getTcpFlags().get("RST") && packet.getTcpFlags().get("ACK");
    }
}
