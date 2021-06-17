import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;

import java.io.IOException;


public class PcapPrinter {

    public static void main(String[] args) {
        try {
            final Pcap pcap=Pcap.openStream("src/main/resources/ictf2010.pcap1");

            pcap.loop(new PacketHandler() {
                @Override
                public boolean nextPacket(Packet packet) throws IOException {

                    if(packet.hasProtocol(Protocol.TCP)) {
                        TCPPacket tcpPacket=(TCPPacket) packet.getPacket(Protocol.TCP);
                        Buffer buffer=tcpPacket.getPayload();
                        if(buffer!=null) {
                            System.out.println("TCP: "+buffer);
                        }
                    }
                    else if(packet.hasProtocol(Protocol.UDP)) {
                        UDPPacket udpPacket=(UDPPacket) packet.getPacket(Protocol.UDP);
                        Buffer buffer=udpPacket.getPayload();
                        if(buffer!=null) {
                            System.out.println("UDP: "+buffer);
                        }
                    }
                    return true;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
