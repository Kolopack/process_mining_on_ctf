package converters;

import io.pkts.packet.IPPacket;
import io.pkts.packet.TCPPacket;
import packets.PcapPacket;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;

public class PacketConverter {

    public static PcapPacket convertPacket(IPPacket ipPacket, TCPPacket tcpPacket) {
        int identification=ipPacket.getIdentification();

        String ipSender=ipPacket.getSourceIP();
        String ipReceiver=ipPacket.getDestinationIP();

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
        result.setiPPayload(ipPayload.get());

        Optional<byte[]> tcpPayload=Optional.of(tcpPacket.getPayload().getArray());
        result.setTcpPayload(tcpPayload.get());

        HashMap<String, Boolean> flags=getFlagMap(tcpPacket);
        result.setTcpFlags(flags);

        return result;
    }

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
