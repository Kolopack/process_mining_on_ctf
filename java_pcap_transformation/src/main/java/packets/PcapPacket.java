package packets;

import java.sql.Timestamp;
import java.util.HashMap;

public class PcapPacket {
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

    public PcapPacket(int identification,
                      String ipSender,
                      String ipReceiver,
                      Timestamp arrivalTime,
                      short fragmentOffset,
                      int ipHeaderLength,
                      byte[] iPPayload,
                      int portSender,
                      int portReceiver,
                      byte[] tcpPayload,
                      long seqNumber,
                      long ackNumber,
                      HashMap<String, Boolean> tcpFlags) {
        this.identification = identification;
        this.ipSender = ipSender;
        this.ipReceiver = ipReceiver;
        ArrivalTime = arrivalTime;
        this.fragmentOffset = fragmentOffset;
        this.ipHeaderLength = ipHeaderLength;
        this.iPPayload = iPPayload;
        this.portSender = portSender;
        this.portReceiver = portReceiver;
        this.tcpPayload = tcpPayload;
        this.seqNumber = seqNumber;
        this.ackNumber = ackNumber;
        this.tcpFlags = tcpFlags;
    }
}
