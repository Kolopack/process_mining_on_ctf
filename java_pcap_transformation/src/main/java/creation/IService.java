package creation;

import packets.PcapPacket;

import java.util.List;

/**
 * Interface to define the method for creating XES out of a list, which then is implemented in all service-classes.
 */
public interface IService {
    /**
     * Declare method to create XES out of a PcapPacket-list
     * @param packetList List<PcapPacket> containing the PcapPackets
     * @param xesPath The path to the XES-file to be created as a String
     */
    void createXESwithList(List<PcapPacket> packetList, String xesPath);
}
