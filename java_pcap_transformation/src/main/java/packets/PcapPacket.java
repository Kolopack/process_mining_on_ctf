package packets;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;

public class PcapPacket implements Serializable {
    //IP-related parameters
    private int identification;
    private String ipSender;
    private String ipReceiver;
    private Timestamp ArrivalTime; //Check if ip time same as tcp time
    private short fragmentOffset;
    private int ipHeaderLength;
    private byte[] iPPayload;

    //TCP-related parameters
    private int portSender;
    private int portReceiver;
    private byte[] tcpPayload;
    private long seqNumber;
    private long ackNumber;
    private HashMap<String, Boolean> tcpFlags;

    public PcapPacket(int identification, String ipSender, String ipReceiver) {
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

    public void setiPPayload(byte[] iPPayload) {
        this.iPPayload = iPPayload;
    }

    public void setPortSender(int portSender) {
        this.portSender = portSender;
    }

    public void setPortReceiver(int portReceiver) {
        this.portReceiver = portReceiver;
    }

    public void setTcpPayload(byte[] tcpPayload) {
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

    @Override
    public String toString() {
        return "PcapPacket{" +
                "identification=" + identification +
                ", ipSender='" + ipSender + '\'' +
                ", ipReceiver='" + ipReceiver + '\'' +
                ", ArrivalTime=" + ArrivalTime +
                ", fragmentOffset=" + fragmentOffset +
                ", ipHeaderLength=" + ipHeaderLength +
                ", iPPayload=" + Arrays.toString(iPPayload) +
                ", portSender=" + portSender +
                ", portReceiver=" + portReceiver +
                ", tcpPayload=" + Arrays.toString(tcpPayload) +
                ", seqNumber=" + seqNumber +
                ", ackNumber=" + ackNumber +
                ", tcpFlags=" + tcpFlags +
                '}';
    }
}
