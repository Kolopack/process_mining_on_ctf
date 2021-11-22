package serviceRepresentation;

import enumerations.Handshakes;
import enumerations.OvercovertPart;
import packets.PcapPacket;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Overcovert {
    private InetAddress teamIP;
    private Integer teamPort;
    private HashMap<Handshakes,PcapPacket> handshakes;
    private HashMap<OvercovertPart,List<PcapPacket>> inbetween;
    private PcapPacket reset;

    public Overcovert(Integer teamPort) {
        this.teamPort=teamPort;
    }

    public HashMap<Handshakes,PcapPacket> getHandshakes() {
        return handshakes;
    }

    public void setHandshakes(HashMap<Handshakes,PcapPacket> handshakes) {
        this.handshakes = handshakes;
    }

    public void setHandshake(Handshakes type, PcapPacket packet) {
        handshakes.put(type,packet);
    }

    public HashMap<OvercovertPart, List<PcapPacket>> getInbetween() {
        return inbetween;
    }

    public void setInbetween(HashMap<OvercovertPart,List<PcapPacket>> inbetween) {
        this.inbetween = inbetween;
    }

    public PcapPacket getReset() {
        return reset;
    }

    public void setReset(PcapPacket reset) {
        this.reset = reset;
    }

    public InetAddress getTeamIP() {
        return teamIP;
    }

    public void setTeamIP(InetAddress teamIP) {
        this.teamIP = teamIP;
    }

    public Integer getTeamPort() {
        return teamPort;
    }

    public void setTeamPort(Integer teamPort) {
        this.teamPort = teamPort;
    }

    @Override
    public String toString() {
        String result="Overcovert{";
        result+="teamport: "+teamPort+"\n";
        result+= "handshake:\n";
        for(Map.Entry<Handshakes, PcapPacket> entry : handshakes.entrySet()) {
            result+=entry.getKey().name()+":\n";
            result+=entry.getValue()+"\n";
        }
        result+="Inbetween:\n";
        for(Map.Entry<OvercovertPart, List<PcapPacket>> entry : inbetween.entrySet()) {
            result+=entry.getKey().name()+":\n";
            result+=entry.getValue()+"\n";
        }
        result+="Reset-packet:\n";
        result+=reset+"}";
        return result;
    }
}
