package xeshandling;

import packets.PcapPacket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class OvercovertEventCreator {

    public static List<PcapPacket> checkForConnectionResets(List<PcapPacket> list) {
        List<PcapPacket> result=new ArrayList<>();

        for(PcapPacket packet : list) {
            if(isFullReset(packet)) {
                result.add(packet);
            }
        }
        return result;
    }

    public static boolean isFullReset(PcapPacket packet) {
        if(packet.getTcpFlags().get("RST") && packet.getTcpFlags().get("ACK")) {
            return true;
        }
        return false;
    }
}
