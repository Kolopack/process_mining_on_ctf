package xeshandling;

import packets.PcapPacket;

import java.util.List;

public class ListManager {

    public static List<PcapPacket> getRestOfList(List<PcapPacket> list, int index) {
        return list.subList(index+1,list.size());
    }
}
