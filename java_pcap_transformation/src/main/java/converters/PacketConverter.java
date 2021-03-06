package converters;

import io.pkts.packet.IPPacket;
import io.pkts.packet.TCPPacket;
import packets.PcapPacket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;

/**
 * Class used for converting the packet-classes of io.pkts to internal class-representation (PcapPacket)
 */
public class PacketConverter {
    /**
     * Method which converts the IPPacket and TCPPacket-objects out of io.pkts-package to the internal
     * representation (Pcappacket)
     * @param ipPacket Object of io.pkts.packet public interface IPPacket
     * @param tcpPacket Object of io.pkts.packet public interface TCPPacket
     * @return Object of PcapPacket (internal)
     */
    public static PcapPacket convertPacket(IPPacket ipPacket, TCPPacket tcpPacket) {
        int identification=ipPacket.getIdentification();

        InetAddress ipSender = null;
        InetAddress ipReceiver=null;
        try {
            ipSender = InetAddress.getByName(ipPacket.getSourceIP());
            ipReceiver = InetAddress.getByName(ipPacket.getDestinationIP());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        PcapPacket result=new PcapPacket(identification,ipSender,ipReceiver);

        assert ipPacket.getArrivalTime() == tcpPacket.getArrivalTime();
        Timestamp timestamp=new Timestamp(ipPacket.getArrivalTime()/1000);
        result.setArrivalTime(timestamp);

        result.setPortSender(tcpPacket.getSourcePort());
        result.setPortReceiver(tcpPacket.getDestinationPort());
        result.setFragmentOffset(ipPacket.getFragmentOffset());
        result.setIpHeaderLength(ipPacket.getHeaderLength());
        result.setSeqNumber(tcpPacket.getSequenceNumber());
        result.setAckNumber(tcpPacket.getAcknowledgementNumber());

        Optional<byte[]> ipPayload= Optional.of(ipPacket.getPayload().getArray());
        if(ipPayload.isPresent()) {
            result.setIPPayload(new String(ipPayload.get(), StandardCharsets.UTF_8));
        }
        else {
            result.setIPPayload(null);
        }

        Optional<TCPPacket> tcpPacketOptional=Optional.of(tcpPacket);
        if(tcpPacketOptional.isPresent()) {
            if(tcpPacketOptional.get().getPayload()!=null) {
                Optional<byte[]> tcpPayloadArray=Optional.of(tcpPacketOptional.get().getPayload().getArray());
                result.setTcpPayload(new String(tcpPayloadArray.get(), StandardCharsets.UTF_8));
            }
            else {
                result.setTcpPayload(null);
            }
            HashMap<String, Boolean> flags=getFlagMap(tcpPacket);
            result.setTcpFlags(flags);
        }

        return result;
    }

    /**
     * Method for converting the flags set inside of the io.pkts-TCPPacket-object into the representation of
     * PcapPacket (internal)
     * @param tcpPacket Object of io.pkts.packet public interface TCPPacket
     * @return Hashmap of String (flag-string) and Boolean (if set true or false),
     * which is the way of storing TCP-flags inside of our PcapPacket-class
     */
    private static HashMap<String, Boolean> getFlagMap(TCPPacket tcpPacket){
        HashMap<String, Boolean> result=new HashMap<>();

        result.put("ACK",tcpPacket.isACK());
        result.put("SYN",tcpPacket.isSYN());
        result.put("RST",tcpPacket.isRST());
        result.put("FIN",tcpPacket.isFIN());
        result.put("PSH",tcpPacket.isPSH());
        result.put("URG",tcpPacket.isURG());
        result.put("CWR",tcpPacket.isCWR());
        result.put("ECE",tcpPacket.isECE());

        return result;
    }

}
