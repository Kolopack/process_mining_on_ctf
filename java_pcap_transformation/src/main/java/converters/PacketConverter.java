package converters;

import io.pkts.packet.IPPacket;
import io.pkts.packet.TCPPacket;
import packets.IpPacket;
import packets.PcapPacket;

import java.sql.Timestamp;

public class PacketConverter {

    /*private static PcapPacket convertPacket(IPPacket ipPacket, TCPPacket tcpPacket) {
        String ipSender=ipPacket.getSourceIP();
        String ipReceiver=ipPacket.getDestinationIP();


        assert ipPacket.getArrivalTime() == tcpPacket.getArrivalTime();
        Timestamp timestamp=new Timestamp(ipPacket.getArrivalTime()/1000);

        String packetName= tcpPacket.getName();
        int portSender=tcpPacket.getSourcePort();
        int portReceiver=tcpPacket.getDestinationPort();

        //Identification
        //FragmentOffset
        //IPheaderLength
        //Payload //Achten of Payload eh nicht null ist

        //Payload TCP
        //Sequence Number
        //Acknowledgement Number
        //Ob flags gesetzt: RST, ACK, usw.
    }*/
}
