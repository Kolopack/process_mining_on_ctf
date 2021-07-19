package packets;

import java.sql.Timestamp;

public class IpPacket {
    private String ipSender;
    private String ipReceiver;

    private Timestamp arrivalTime;

    public IpPacket(String ipSender, String ipReceiver, Timestamp arrivalTime) {
        this.ipSender = ipSender;
        this.ipReceiver = ipReceiver;
        this.arrivalTime = arrivalTime;
    }
}
