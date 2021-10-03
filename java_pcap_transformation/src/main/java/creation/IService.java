package creation;

import packets.PcapPacket;

import java.util.List;

public interface IService {


    void createXESwithList(List<PcapPacket> packetList, String xesPath);
}
