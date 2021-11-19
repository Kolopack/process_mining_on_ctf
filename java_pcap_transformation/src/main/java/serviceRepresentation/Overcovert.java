package serviceRepresentation;

import packets.PcapPacket;

import java.net.InetAddress;
import java.util.List;

public class Overcovert {
    private InetAddress teamIP;
    private InetAddress serviceIP;
    private List<PcapPacket> handshakes;
    private List<PcapPacket> inbetween;
    private PcapPacket reset;

    public Overcovert(InetAddress teamIP, InetAddress serviceIP) {
        this.teamIP = teamIP;
        this.serviceIP = serviceIP;
    }

    public List<PcapPacket> getHandshakes() {
        return handshakes;
    }

    public void setHandshakes(List<PcapPacket> handshakes) {
        this.handshakes = handshakes;
    }

    public List<PcapPacket> getInbetween() {
        return inbetween;
    }

    public void setInbetween(List<PcapPacket> inbetween) {
        this.inbetween = inbetween;
    }

    public PcapPacket getReset() {
        return reset;
    }

    public void setReset(PcapPacket reset) {
        this.reset = reset;
    }

    @Override
    public String toString() {
        String result="Overcovert{";
        result+="teamIP=" + teamIP + ", serviceIP=" + serviceIP+"\n";
        result+= "handshake:\n";
        for(PcapPacket packet : handshakes) {
            result+=packet+"\n";
        }
        result+="Inbetween:\n";
        for(PcapPacket packet : inbetween) {
            result+=packet+"\n";
        }
        result+="Reset-packet:\n";
        result+=reset+"}";
        return result;
    }
}
