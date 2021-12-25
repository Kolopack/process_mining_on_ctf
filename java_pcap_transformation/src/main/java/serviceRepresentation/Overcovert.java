package serviceRepresentation;

import enumerations.Finishes;
import enumerations.Handshakes;
import packets.PcapPacket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Overcovert.
 * Is used as representation of one Overcovert-trace and holds all required data.
 * For internal usage in the program.
 */
public class Overcovert {
    /**
     * IP-address of the team as java.net.InetAddress
     */
    private InetAddress teamIP;
    /**
     * Port the team is using for this Overcovert-trace
     */
    private final Integer teamPort;
    /**
     * IP-address of the service as java.net.InetAddress
     */
    private InetAddress serviceIP;
    /**
     * Port the team is using for this Overcovert-trace
     */
    private  Integer servicePort;
    /**
     * HashMap containing the Handshake packets.
     * Key: Enum indicating which part of Three-Way-Handshake (First, Second, Third)
     * Value: Affiliated PcapPacket
     */
    private HashMap<Handshakes,PcapPacket> handshakes;
    /**
     * List of PcapPackets which are between Handshake and finishing and so cover the real logic of this process
     */
    private List<PcapPacket> inbetween;
    /**
     * HashMap containing the Finishes packets.
     * Key: Enum indicating which part of TCP-finishing process (First, Second, Third)
     * Value: Affiliated PcapPacket
     */
    private List<HashMap<Finishes, PcapPacket>> finishes;
    /**
     * Same HashMap, but with the current (last) Finishes, as used in OvercovertReader-class
     */
    private HashMap<Finishes, PcapPacket> lastfinishes;
    /**
     * The PcapPacket which makes the resetting of the connection (always in Overcovert)
     */
    private PcapPacket reset;

    /**
     * To indicate if this overcovert is already finished or not (so after handshake and reset are set)
     */
    private boolean isFinished;

    /**
     * Instantiates a new Overcovert.
     *
     * @param teamPort the team port
     */
    public Overcovert(Integer teamPort) {
        handshakes=new HashMap<>();
        inbetween=new ArrayList<>();
        lastfinishes =new HashMap<>();
        finishes=new ArrayList<>();
        this.teamPort=teamPort;
        this.isFinished=false;
    }

    /**
     * Gets handshakes.
     *
     * @return the handshakes
     */
    public HashMap<Handshakes,PcapPacket> getHandshakes() {
        return handshakes;
    }

    /**
     * Sets handshakes.
     *
     * @param handshakes the handshakes
     */
    public void setHandshakes(HashMap<Handshakes,PcapPacket> handshakes) {
        this.handshakes = handshakes;
    }

    /**
     * Gets inbetween.
     *
     * @return the inbetween
     */
    public List<PcapPacket> getInbetween() {
        return inbetween;
    }

    /**
     * Sets inbetween.
     *
     * @param inbetween the inbetween
     */
    public void setInbetween(List<PcapPacket> inbetween) {
        this.inbetween = inbetween;
    }

    /**
     * Gets reset.
     *
     * @return the reset
     */
    public PcapPacket getReset() {
        return reset;
    }

    /**
     * Sets reset.
     *
     * @param reset the reset
     */
    public void setReset(PcapPacket reset) {
        this.reset = reset;
    }

    /**
     * Gets team ip.
     *
     * @return the team ip
     */
    public InetAddress getTeamIP() {
        return teamIP;
    }

    /**
     * Sets team ip.
     *
     * @param teamIP the team ip
     */
    public void setTeamIP(InetAddress teamIP) {
        this.teamIP = teamIP;
    }

    /**
     * Gets team port.
     *
     * @return the team port
     */
    public Integer getTeamPort() {
        return teamPort;
    }

    /**
     * Gets lastfinishes.
     *
     * @return the lastfinishes
     */
    public HashMap<Finishes,PcapPacket> getLastfinishes() {
        return lastfinishes;
    }

    /**
     * Sets lastfinishes.
     *
     * @param lastfinishes the lastfinishes
     */
    public void setLastfinishes(HashMap<Finishes,PcapPacket> lastfinishes) {
        this.lastfinishes = lastfinishes;
    }

    /**
     * Is finished boolean.
     *
     * @return the boolean
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Sets finished.
     *
     * @param finished the finished
     */
    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    /**
     * Gets finishes.
     *
     * @return the finishes
     */
    public List<HashMap<Finishes, PcapPacket>> getFinishes() {
        return finishes;
    }

    /**
     * Sets finishes.
     *
     * @param finishes the finishes
     */
    public void setFinishes(List<HashMap<Finishes, PcapPacket>> finishes) {
        this.finishes = finishes;
    }

    /**
     * Gets service ip.
     *
     * @return the service ip
     */
    public InetAddress getServiceIP() {
        return serviceIP;
    }

    /**
     * Sets service ip.
     *
     * @param serviceIP the service ip
     */
    public void setServiceIP(InetAddress serviceIP) {
        this.serviceIP = serviceIP;
    }

    /**
     * Gets service port.
     *
     * @return the service port
     */
    public Integer getServicePort() {
        return servicePort;
    }

    /**
     * Sets service port.
     *
     * @param servicePort the service port
     */
    public void setServicePort(Integer servicePort) {
        this.servicePort = servicePort;
    }

    @Override
    public String toString() {
        StringBuilder result= new StringBuilder("Overcovert{");
        result.append("teamport: ").append(teamPort).append("\n");
        result.append("handshake:\n");
        for(Map.Entry<Handshakes, PcapPacket> entry : handshakes.entrySet()) {
            result.append(entry.getKey().name()).append(":\n");
            result.append(entry.getValue()).append("\n");
        }
        result.append("Inbetween:\n");
        for(PcapPacket entry : inbetween) {
            result.append(entry).append(":\n");
        }
        result.append("Reset-packet:\n");
        result.append(reset).append("}");
        return result.toString();
    }
}