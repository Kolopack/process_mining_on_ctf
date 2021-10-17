package creation;

import packets.PcapPacket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class OvercovertService extends AbstractXESService implements IService{
    private static final String OVERCOVERT="Overcovert";
    private static final String OVERCOVERT_IP_STRING="10.14.1.10";
    private static InetAddress OVERCOVERT_IP;

    static {
        try {
            OVERCOVERT_IP = InetAddress.getByName(OVERCOVERT_IP_STRING);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public OvercovertService(String teamName, InetAddress teamIP) {
        super(OVERCOVERT, teamName, teamIP,OVERCOVERT_IP);
    }

    @Override
    public void createXESwithList(List<PcapPacket> packetList, String xesPath) {
        this.packetList=packetList;
    }

}
