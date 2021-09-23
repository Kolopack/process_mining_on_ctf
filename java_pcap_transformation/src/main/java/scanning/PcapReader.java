package scanning;

import converters.PacketConverter;
import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.IPPacket;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;
import packets.PcapPacket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.logging.Logger;

public class PcapReader {
    Logger logger=Logger.getLogger(PcapReader.class.getName());
    private static final String pcapEnding=".*\\.pcap.*";

    private File directoryPath;
    private Path temporaryStoringPath;
    private File[] fileList;
    private InetAddress ipService;
    private InetAddress ipTeam;
    private int packetCounter;
    private int packetTcpCounter;

    public void importPcap(String pathToDirectory, String temporarySavingDirectory) {
        directoryPath=new File(pathToDirectory);
        temporaryStoringPath= Paths.get(temporarySavingDirectory);
        importFilepath();
    }

    private void importFilepath() {
        fileList=directoryPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches(pcapEnding);
                //return name.endsWith(pcapEnding);
            }
        });
        printOutFileList();
    }
    //Just for testing-purposes
    private void printOutFileList() {
        for(File file : fileList) {
            System.out.println("File-Name: "+file.getName());
        }
    }

    public void readFiles(InetAddress ipTeam, InetAddress ipService) {
        this.ipTeam=ipTeam;
        this.ipService=ipService;

        for(File file : fileList) {
            filterPCAP(file.getPath(),ipTeam,ipService);
            logger.info("File: "+file.getName()+" finished. "+packetCounter+"/"+packetTcpCounter+" total/TCP");
        }
    }

    private void filterPCAP(String filePath, InetAddress ipTeam, InetAddress ipService) {
        try {
            Pcap pcap = Pcap.openStream(filePath);
            pcap.loop(new PacketHandler() {
                @Override
                public boolean nextPacket(Packet packet) throws IOException {
                    ++packetCounter;

                    if(packet.hasProtocol(Protocol.IPv4)) {
                        IPPacket ipPacket=(IPPacket) packet.getPacket(Protocol.IPv4);

                        InetAddress ipSource=InetAddress.getByName(ipPacket.getSourceIP());
                        InetAddress ipDestination=InetAddress.getByName(ipPacket.getDestinationIP());

                        if(iPisSenderOrReceiver(ipSource,ipDestination)) {
                            if(packet.hasProtocol(Protocol.TCP)) {
                                //TCPPacket tcpPacket=(TCPPacket) packet.getPacket(Protocol.TCP);
                                ++packetTcpCounter;
                                //PcapPacket myPacket=PacketConverter.convertPacket(ipPacket,tcpPacket);
                            }
                        }
                    }
                    return true;
                }
            });
        } catch (FileNotFoundException e) {
            logger.severe(e.getMessage());
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    private boolean iPisSenderOrReceiver(InetAddress ipSource, InetAddress ipDestination) {
        if(ipSource.equals(ipService) && ipDestination.equals(ipTeam)) {
            return true;
        }
        if(ipSource.equals(ipTeam) && ipDestination.equals(ipService)) {
            return true;
        }
        return false;
    }
}