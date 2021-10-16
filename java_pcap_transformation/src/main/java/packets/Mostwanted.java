package packets;

import enumerations.MostwantedPart;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

public class Mostwanted {
    private InetAddress team;
    private InetAddress service;
    private HashMap<MostwantedPart,List<PcapPacket>> packets;
    private boolean success;
    private String flag;

    public Mostwanted(InetAddress team, InetAddress service, HashMap<MostwantedPart, List<PcapPacket>> packets) {
        this.team=team;
        this.service=service;
        this.packets=packets;
    }

    public void setSuccess(boolean success, String flag) {
        this.success=success;
        this.flag=flag;
    }

    public List<PcapPacket> getThreeWayHandshakePackets() {
        return packets.get(MostwantedPart.HANDSHAKE);
    }

    public List<PcapPacket> getTCPFinishingPackets() {
        return packets.get(MostwantedPart.FINISHING);
    }

    public List<PcapPacket> getPSHACKAttacks() {
        return packets.get(MostwantedPart.PSHACK);
    }

    public boolean wasSuccessful() {
        return success;
    }
}
