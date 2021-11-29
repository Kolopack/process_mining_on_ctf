package creation;

import packets.PcapPacket;
import xeshandling.XESManager;

import javax.swing.text.Element;
import java.util.List;

public interface IService {

    void createXESwithList(List<PcapPacket> packetList, String xesPath);
}
