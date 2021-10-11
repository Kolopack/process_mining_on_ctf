package xeshandling;

import packets.PcapPacket;

import java.util.ArrayList;
import java.util.List;

public class ListManager {

    public static List<PcapPacket> getRestOfList(List<PcapPacket> list, int index) {
        List<PcapPacket> result=new ArrayList<>();
        index++;

        for(; index<list.size();++index) {
            result.add(list.get(index));
        }
        return result;
    }
}
