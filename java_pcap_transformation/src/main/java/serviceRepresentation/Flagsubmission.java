package serviceRepresentation;

import packets.PcapPacket;

import java.net.InetAddress;
import java.util.List;

public class Flagsubmission {
    private InetAddress team;
    private InetAddress service;
    private List<PcapPacket> handshakes;
    private List<PcapPacket> finishes;
    private List<PcapPacket> packetsInbetween;

    public Flagsubmission(InetAddress team, InetAddress service) {
        this.team = team;
        this.service = service;
    }

    public List<PcapPacket> getHandshakes() {
        return handshakes;
    }

    public void setHandshakes(List<PcapPacket> handshakes) {
        this.handshakes = handshakes;
    }

    public List<PcapPacket> getFinishes() {
        return finishes;
    }

    public void setFinishes(List<PcapPacket> finishes) {
        this.finishes = finishes;
    }

    public List<PcapPacket> getPacketsInbetween() {
        return packetsInbetween;
    }

    public void setPacketsInbetween(List<PcapPacket> packetsInbetween) {
        this.packetsInbetween = packetsInbetween;
    }
}
