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

    public int getFullAmountOfPackets() {
        int result=0;
        for(Map.Entry<MostwantedPart, List<PcapPacket>> elem : packets.entrySet()) {
            result=result+elem.getValue().size();
        }
        return result;
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
                ", flag='" + flag + '\''+
                " , amount of packets="+getFullAmountOfPackets()+"\n";
                result+= "{"+MostwantedPart.HANDSHAKE.name()+":\n";
                List<PcapPacket> handshake=packets.get(MostwantedPart.HANDSHAKE);
                for(PcapPacket packet : handshake) {
                    result+= packet.toString()+"\n";
                }
                result+="}\n";

                result+= "{"+MostwantedPart.PSHACK.name()+":\n";
                List<PcapPacket> pshack=packets.get(MostwantedPart.PSHACK);
                for(PcapPacket packet : pshack) {
                    result+= packet.toString()+"\n";
                }
                result+="}\n";

                result+= "{"+MostwantedPart.FINISHING.name()+":\n";
                List<PcapPacket> finish=packets.get(MostwantedPart.FINISHING);
                for(PcapPacket packet : finish) {
                    result+= packet.toString()+"\n";
                }

                result+="};";
                return result;
    }
}
