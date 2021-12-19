package packets;

import java.io.Serializable;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * The type Pcap packet.
 * This class is for the internal handling of a packet of the PCAP-files.
 * It has to implement serializable in order to enable the serialized storing in the temp-storage.
 */
public class PcapPacket implements Serializable {
    /**
     * IP-RELATED PARAMETERS:
     *
     * Identification (number) of the packet
     */
    private final int identification;
    /**
     * IP-address of the sender as java.net.InetAddress
     */
    private final InetAddress ipSender;
    /**
     * IP-address of the receiver as java.net.InetAddress
     */
    private final InetAddress ipReceiver;
    /**
     * Arrival-time of the packet as Timestamp, out of the IP-protocol-data of the packet
     */
    private Timestamp ArrivalTime;
    /**
     * FragmentOffset (when information is sent over several packets)
     */
    private short fragmentOffset;
    /**
     * Length of the IP-header as Integer (from the IP-protocol part)
     */
    private int ipHeaderLength;
    /**
     * Payload of the IP-packet as String
     */
    private String iPPayload;

    /**
     * TCP-RELATED PARAMETERS:
     *
     * Port of the Sender of the packet as Integer
     */
    private int portSender;
    /**
     * Port of the receiver of the packet as Integer
     */
    private int portReceiver;
    /**
     * Payload of the TCP-packet (based on the TCP-protocol)
     */
    private String tcpPayload;
    /**
     * Sequence-number of the packet (based on TCP)
     */
    private long seqNumber;
    /**
     * Acknowledgement-number of the packet (based on TCP)
     */
    private long ackNumber;
    /**
     * HashMap containing the different TCP-flag types (ACK, PSH,...) as String-keys and booleans
     * indicating whether the flag is set or not as values of the map.
     */
    private HashMap<String, Boolean> tcpFlags;

    /**
     * Instantiates a new Pcap packet.
     *
     * @param identification the identification
     * @param ipSender       the ip sender
     * @param ipReceiver     the ip receiver
     */
    public PcapPacket(int identification, InetAddress ipSender, InetAddress ipReceiver) {
        this.identification = identification;
        this.ipSender = ipSender;
        this.ipReceiver = ipReceiver;
    }

    /**
     * Sets arrival time.
     *
     * @param arrivalTime the arrival time
     */
    public void setArrivalTime(Timestamp arrivalTime) {
        ArrivalTime = arrivalTime;
    }

    /**
     * Sets fragment offset.
     *
     * @param fragmentOffset the fragment offset
     */
    public void setFragmentOffset(short fragmentOffset) {
        this.fragmentOffset = fragmentOffset;
    }

    /**
     * Sets ip header length.
     *
     * @param ipHeaderLength the ip header length
     */
    public void setIpHeaderLength(int ipHeaderLength) {
        this.ipHeaderLength = ipHeaderLength;
    }

    /**
     * Sets ip payload.
     *
     * @param iPPayload the p payload
     */
    public void setIPPayload(String iPPayload) {
        this.iPPayload = iPPayload;
    }

    /**
     * Sets port sender.
     *
     * @param portSender the port sender
     */
    public void setPortSender(int portSender) {
        this.portSender = portSender;
    }

    /**
     * Sets port receiver.
     *
     * @param portReceiver the port receiver
     */
    public void setPortReceiver(int portReceiver) {
        this.portReceiver = portReceiver;
    }

    /**
     * Sets tcp payload.
     *
     * @param tcpPayload the tcp payload
     */
    public void setTcpPayload(String tcpPayload) {
        this.tcpPayload = tcpPayload;
    }

    /**
     * Sets seq number.
     *
     * @param seqNumber the seq number
     */
    public void setSeqNumber(long seqNumber) {
        this.seqNumber = seqNumber;
    }

    /**
     * Sets ack number.
     *
     * @param ackNumber the ack number
     */
    public void setAckNumber(long ackNumber) {
        this.ackNumber = ackNumber;
    }

    /**
     * Sets tcp flags.
     *
     * @param tcpFlags the tcp flags
     */
    public void setTcpFlags(HashMap<String, Boolean> tcpFlags) {
        this.tcpFlags = tcpFlags;
    }

    /**
     * Gets arrival time.
     *
     * @return the arrival time
     */
    public Timestamp getArrivalTime() {
        return ArrivalTime;
    }

    /**
     * Gets tcp flags.
     *
     * @return the tcp flags
     */
    public HashMap<String, Boolean> getTcpFlags() {
        return tcpFlags;
    }

    /**
     * Gets seq number.
     *
     * @return the seq number
     */
    public long getSeqNumber() {
        return seqNumber;
    }

    /**
     * Gets ack number.
     *
     * @return the ack number
     */
    public long getAckNumber() {
        return ackNumber;
    }

    /**
     * Gets ip sender.
     *
     * @return the ip sender
     */
    public InetAddress getIpSender() {
        return ipSender;
    }

    /**
     * Gets ip receiver.
     *
     * @return the ip receiver
     */
    public InetAddress getIpReceiver() {
        return ipReceiver;
    }

    /**
     * Gets ip payload.
     *
     * @return the ip payload
     */
    public String getIPPayload() {
        return iPPayload;
    }

    /**
     * Gets tcp payload.
     *
     * @return the tcp payload
     */
    public String getTcpPayload() {
        return tcpPayload;
    }

    /**
     * Gets port sender.
     *
     * @return the port sender
     */
    public int getPortSender() {
        return portSender;
    }

    /**
     * Gets port receiver.
     *
     * @return the port receiver
     */
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
