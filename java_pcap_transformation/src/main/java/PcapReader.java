import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.IPPacket;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

public class PcapReader {

    public static void main(String[] args) {
        String filePath="./target/";
        String fileName="testXML";
        XESManager manager=new XESManager(filePath,fileName);
/*
        try {
            final Pcap pcap=Pcap.openStream("src/main/resources/ictf2010.pcap1");

            pcap.loop(new PacketHandler() {
                @Override
                public boolean nextPacket(Packet packet) throws IOException {
                    IPPacket ipPacket=(IPPacket) packet.getPacket(Protocol.IPv4);

                    if(packet.hasProtocol(Protocol.TCP)) {
                        TCPPacket tcpPacket=(TCPPacket) packet.getPacket(Protocol.TCP);

                        /*Buffer buffer=tcpPacket.getPayload();
                        if(buffer!=null) {
                            System.out.println("TCP: "+buffer);
                        }*/
         /*               Timestamp timestamp= new Timestamp(tcpPacket.getArrivalTime()/1000);

                        System.out.println("Name: "+tcpPacket.getName());
                        System.out.println("TimestampTCP: "+timestamp);
                        System.out.println("Source-Host: "+ipPacket.getSourceIP());
                        System.out.println("Destination-Host: "+ipPacket.getDestinationIP());
                        System.out.println("Source-Port: "+tcpPacket.getSourcePort());
                        System.out.println("Destination-Port:"+tcpPacket.getDestinationPort());
                        //System.out.println(packet.getPacket(Protocol.TCP).getPayload());
                    }
                    else if(packet.hasProtocol(Protocol.UDP)) {
                        UDPPacket udpPacket=(UDPPacket) packet.getPacket(Protocol.UDP);
                        /*Buffer buffer=udpPacket.getPayload();
                        if(buffer!=null) {
                            System.out.println("UDP: "+buffer);
                        }*/

              /*          System.out.println("Name: "+udpPacket.getName());
                        System.out.println("Source-Host: "+ipPacket.getSourceIP());
                        System.out.println("Destination-Host: "+ipPacket.getDestinationIP());
                        System.out.println("Source-Port: "+udpPacket.getSourcePort());
                        System.out.println("Destination-Port:"+udpPacket.getDestinationPort());
                    }
                    return true;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    //private
}
