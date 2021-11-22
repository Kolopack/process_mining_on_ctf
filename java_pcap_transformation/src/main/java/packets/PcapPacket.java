package packets;

import java.io.Serializable;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;

public class PcapPacket implements Serializable {
    //IP-related parameters
    private int identification;
    private InetAddress ipSender;
    private InetAddress ipReceiver;
    private Timestamp ArrivalTime; //Check if ip time same as tcp time
    private short fragmentOffset;
    private int ipHeaderLength;
    private String iPPayload;

    //TCP-related parameters
    private int portSender;
    private int portReceiver;
    private String tcpPayload;
    private long seqNumber;
    private long ackNumber;
    private HashMap<String, Boolean> tcpFlags;

    public PcapPacket(int identification, InetAddress ipSender, InetAddress ipReceiver) {
        this.identification = identification;
        this.ipSender = ipSender;
        this.ipReceiver = ipReceiver;
    }

    public void setArrivalTime(Timestamp arrivalTime) {
        ArrivalTime = arrivalTime;
    }

    public void setFragmentOffset(short fragmentOffset) {
        this.fragmentOffset = fragmentOffset;
    }

    public void setIpHeaderLength(int ipHeaderLength) {
        this.ipHeaderLength = ipHeaderLength;
    }

    public void setiPPayload(String iPPayload) {
        this.iPPayload = iPPayload;
    }

    public void setPortSender(int portSender) {
        this.portSender = portSender;
    }

    public void setPortReceiver(int portReceiver) {
        this.portReceiver = portReceiver;
    }

    public void setTcpPayload(String tcpPayload) {
        this.tcpPayload = tcpPayload;
    }

    public void setSeqNumber(long seqNumber) {
        this.seqNumber = seqNumber;
    }

    public void setAckNumber(long ackNumber) {
        this.ackNumber = ackNumber;
    }

    public void setTcpFlags(HashMap<String, Boolean> tcpFlags) {
        this.tcpFlags = tcpFlags;
    }

    public Timestamp getArrivalTime() {
        return ArrivalTime;
    }

    public HashMap<String, Boolean> getTcpFlags() {
        return tcpFlags;
    }

    public long getSeqNumber() {
        return seqNumber;
    }

    public long getAckNumber() {
        return ackNumber;
    }

    public InetAddress getIpSender() {
        return ipSender;
    }

    public InetAddress getIpReceiver() {
        return ipReceiver;
    }

    public String getiPPayload() {
        return iPPayload;
    }

    public String getTcpPayload() {
        return tcpPayload;
    }

    public int getPortSender() {
        return portSender;
    }

    public int getPortReceiver() {
        return portReceiver;
    }

    @Override
    public String toString() {
        return "PcapPacket{" +
                "identification=" + identification +
                ", ipSender='" + ipSender.getHostName() + '\'' +
                ", ipReceiver='" + ipReceiver.getHostName() + '\'' +
                ", ArrivalTime=" + ArrivalTime +
                ", fragmentOffset=" + fragmentOffset +
                ", ipHeaderLength=" + ipHeaderLength +
                ", iPPayload=" + iPPayload +
                ", portSender=" + portSender +
                ", portReceiver=" + portReceiver +
                ", tcpPayload=" + tcpPayload +
                ", seqNumber=" + seqNumber +
                ", ackNumber=" + ackNumber +
                ", tcpFlags=" + tcpFlags +
                '}';
    }
}
