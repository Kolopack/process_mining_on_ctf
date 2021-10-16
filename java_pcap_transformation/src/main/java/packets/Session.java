package packets;

import java.util.ArrayList;
import java.util.List;

public class Session {
    private Integer certainIndex;
    private List<PcapPacket> packets;

    public Session() {
        certainIndex =null;
        packets=new ArrayList<>();
    }

    public Session(int certainIndex, int indexLastPacket, List<PcapPacket> packets) {
        this.certainIndex = certainIndex;
        this.packets = packets;
    }

    public int getCertainIndex() {
        return certainIndex;
    }

    public List<PcapPacket> getPackets() {
        return packets;
    }

    public PcapPacket getPacketByIndex(int index) {
        return packets.get(index);
    }

    public void setCertainIndex(int firstPacket) {
        this.certainIndex = firstPacket;
    }

    public void setPackets(List<PcapPacket> packets) {
        this.packets = packets;
    }

    @Override
    public String toString() {
        String print= "Session{" +
                "indexFirstPacket=" + certainIndex + "\n";
                print+="The packets involved:\n";
                for(PcapPacket  packet : packets) {
                    print+= packet+"\n";
                }
                print+="};";
        return print;
    }
}
