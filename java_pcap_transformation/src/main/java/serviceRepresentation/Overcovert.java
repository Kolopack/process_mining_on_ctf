package serviceRepresentation;

import enumerations.Finishes;
import enumerations.Handshakes;
import packets.PcapPacket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Overcovert {
    private InetAddress teamIP;
    private Integer teamPort;
    private InetAddress serviceIP;
    private  Integer servicePort;
    private HashMap<Handshakes,PcapPacket> handshakes;
    private List<PcapPacket> inbetween;
    private List<HashMap<Finishes, PcapPacket>> finishes;
    private HashMap<Finishes, PcapPacket> lastfinishes;
    private PcapPacket reset;

    /**
     * To indicate if this overcovert is already finished or not (so after handshake and reset are set
     */
    private boolean isFinished;

    public Overcovert(Integer teamPort) {
        handshakes=new HashMap<>();
        inbetween=new ArrayList<>();
        lastfinishes =new HashMap<>();
        finishes=new ArrayList<>();
        this.teamPort=teamPort;
        this.isFinished=false;
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

    public HashMap<Finishes,PcapPacket> getLastfinishes() {
        return lastfinishes;
    }

    public void setLastfinishes(HashMap<Finishes,PcapPacket> lastfinishes) {
        this.lastfinishes = lastfinishes;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public List<HashMap<Finishes, PcapPacket>> getFinishes() {
        return finishes;
    }

    public void setFinishes(List<HashMap<Finishes, PcapPacket>> finishes) {
        this.finishes = finishes;
    }

    public void addFinish(HashMap<Finishes,PcapPacket> finish) {
        finishes.add(finish);
    }

    public InetAddress getServiceIP() {
        return serviceIP;
    }

    public void setServiceIP(InetAddress serviceIP) {
        this.serviceIP = serviceIP;
    }

    public Integer getServicePort() {
        return servicePort;
    }

    public void setServicePort(Integer servicePort) {
        this.servicePort = servicePort;
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
        for(PcapPacket entry : inbetween) {
            result+=entry+":\n";
        }
        result+="Reset-packet:\n";
        result+=reset+"}";
        return result;
    }
}
