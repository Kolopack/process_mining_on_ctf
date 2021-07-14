import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.IPPacket;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.logging.Logger;

public class PcapReader {
    Logger logger=Logger.getLogger(PcapReader.class.getName());
    private static final String pcapEnding=".*\\.pcap.*";

    private File directoryPath;
    private File[] fileList;

    public void read(String pathToDirectory) {
        directoryPath=new File(pathToDirectory);
        readAllFiles();
    }

    private void readAllFiles() {
        fileList=directoryPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches(pcapEnding);
                //return name.endsWith(pcapEnding);
            }
        });
        printOutFileList();
    }

    private void printOutFileList() {
        for(File file : fileList) {
            System.out.println("File-Name: "+file.getName());
        }
    }

    /*public static void main(String[] args) {


        String filePath="./target/";
        String fileName="testXML";
        XESManager manager=new XESManager(filePath,fileName);

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
        }
    }*/

    //private
}
