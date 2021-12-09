package xeshandling;

import packets.PcapPacket;

import java.util.ArrayList;
import java.util.List;

public class OvercovertEventCreator {

    public static boolean isFullReset(PcapPacket packet) {
        if(packet.getTcpFlags().get("RST") && packet.getTcpFlags().get("ACK")) {
            return true;
        }
        return false;
    }
}
