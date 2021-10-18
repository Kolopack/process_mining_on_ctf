package packets;

import enumerations.MostwantedPart;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mostwanted {
    private InetAddress team;
    private InetAddress service;
    private HashMap<MostwantedPart,List<PcapPacket>> packets;
    private boolean success;
    private String flag;

    public Mostwanted(InetAddress team, InetAddress service) {
        this.team=team;
        this.service=service;
    }

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

    public void setPackets(HashMap<MostwantedPart, List<PcapPacket>> packets) {
        this.packets = packets;
    }

    @Override
    public String toString() {
        String result="";
        result+= "Mostwanted{" +
                "team=" + team +
                ", service=" + service +
                ", success=" + success +
                ", flag='" + flag + '\'';
                for(Map.Entry<MostwantedPart, List<PcapPacket>> entry : packets.entrySet()) {
                    result +="\nPart: "+entry.getKey()+"\n";
                    for(PcapPacket packet : entry.getValue()) {
                        result+= packet.toString()+"\n";
                    }
                }
                result+="};";
                return result;
    }
}
