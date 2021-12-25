package xeshandling;

import packets.PcapPacket;

import java.util.List;

/**
 * The type List manager.
 * This class holds static methods used when dealing and editing Lists in the program
 */
public class ListManager {

    /**
     * Gets rest of list.
     *Method which only returns the rest of the list, so the entries after the given index.
     * @param list  the list of PcapPackets to get the rest of
     * @param index the index which cuts the list and only the entries in the list after this index are returned
     * @return the rest of list
     */
    public static List<PcapPacket> getRestOfList(List<PcapPacket> list, int index) {
        return list.subList(index+1,list.size());
    }
}
