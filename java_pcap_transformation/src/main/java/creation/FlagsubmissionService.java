package creation;

import exceptions.PacketListIsEmptyException;
import exceptions.UnavailableException;
import packets.PcapPacket;
import packets.Session;
import serviceRepresentation.Flagsubmission;
import xeshandling.DefaultEventCreator;
import xeshandling.FlagsubmissionEventCreator;
import xeshandling.XESManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class FlagsubmissionService extends AbstractXESService implements IService{
    private static final String FLAGSUBMISSION="Flagsubmission";
    private static final String FLAGSUBMISSION_IP_STRING="10.16.13.37";
    private static InetAddress FLAGSUBMISSION_IP;

    static {
        try {
            FLAGSUBMISSION_IP=InetAddress.getByName(FLAGSUBMISSION_IP_STRING);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public FlagsubmissionService(String teamName, InetAddress teamIP, String teamMask) {
        super(FLAGSUBMISSION, teamName, teamIP, teamMask, FLAGSUBMISSION_IP);
    }

    @Override
    public void createXESwithList(List<PcapPacket> packetList, String xesPath) {
        if(packetList.isEmpty()) {
            throw new PacketListIsEmptyException();
        }

        this.packetList=packetList;

        try {
            if (isOrderOfPacketsTrue()) {
                logger.info("Packets are in correct order");
            } else {
                throw new UnavailableException();
            }
        } catch (UnavailableException e) {
            e.printStackTrace();
        }

        XESManager xesManager=new XESManager(xesPath,FLAGSUBMISSION+"_"+getTeamName());

        List<Session> handshakes= DefaultEventCreator.checkForThreeWayHandshake(packetList);
        List<List<PcapPacket>> finishes=DefaultEventCreator.checkForFinishing(packetList);

        List<Flagsubmission> flagsubmissions= FlagsubmissionEventCreator.buildFlagsubmissions(handshakes,finishes,packetList,getTeamIP(),FLAGSUBMISSION_IP);
        System.out.println("The following Flagsubmissions were detected: "+flagsubmissions.size());


    }
}
