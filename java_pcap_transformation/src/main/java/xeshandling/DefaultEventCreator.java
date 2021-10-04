package xeshandling;

import javafx.util.Pair;
import packets.PcapPacket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DefaultEventCreator {
    private static Logger logger=Logger.getLogger(DefaultEventCreator.class.getName());

    public static List<List> checkForThreeWayHandshake(List<PcapPacket> list) {
        List<List> result=new ArrayList<>();

        Long seqA;
        InetAddress client;
        InetAddress server;

        for(int i=0; i<list.size();++i) {

            //Current packet
            PcapPacket current=list.get(i);
            if(current.getTcpFlags().get("SYN")) {
                client=current.getIpSender();
                server=current.getIpReceiver();
                seqA=current.getSeqNumber();
                List<PcapPacket> rest=getRestOfList(list,i);

                Pair<Long, PcapPacket> secondPacket= checkForSecondPacketThreeWayHandshake(rest,server,seqA);
                Long seqB=secondPacket.getKey();

                if(seqB!=null) {


                    Pair<Boolean, PcapPacket> thirdPacket= checkForThirdPacketThreeWayHandshake(rest,seqB,(seqA+1),client);
                    if(thirdPacket.getKey()) {
                        List<PcapPacket> handshake=new ArrayList();
                        handshake.add(current);
                        handshake.add(secondPacket.getValue());
                        handshake.add(thirdPacket.getValue());

                        result.add(handshake);
                    }
                }
            }
        }
        return result;
    }

    private static List<PcapPacket> getRestOfList(List<PcapPacket> list, int index) {
        List<PcapPacket> result=new ArrayList<>();
        index++;

        for(; index<list.size();++index) {
            result.add(list.get(index));
        }
        return result;
    }

    private static Pair checkForSecondPacketThreeWayHandshake(List<PcapPacket> list, InetAddress server, Long seqFirst) {
        Long result=null;

        for(PcapPacket packet : list) {
            if(packet.getIpSender().equals(server) && packet.getAckNumber()==(seqFirst+1)){
                if(packet.getTcpFlags().get("SYN") && packet.getTcpFlags().get("ACK")) {
                    result=packet.getSeqNumber();
                    return new Pair(result, packet);
                }
            }
        }
        return new Pair(result,null);
    }

    private static Pair checkForThirdPacketThreeWayHandshake(List<PcapPacket> list, Long seqSecond, Long ackFirst, InetAddress client) {
        boolean result=false;

        for(PcapPacket packet : list) {
            if(packet.getIpSender().equals(client) && packet.getAckNumber()==(seqSecond+1)
                && packet.getSeqNumber()==ackFirst) {
                if(packet.getTcpFlags().get("ACK")) {
                    result=true;
                    return new Pair<>(result, packet);
                }
            }
        }
        return new Pair<>(result, null);
    }

    public static List<List> checkForFinishing(List<PcapPacket> list) {
        List<List> result=new ArrayList<>();

        Long seqA;
        InetAddress partnerA;
        InetAddress partnerB;

        for(int i=0; i<list.size();++i) {
            PcapPacket current=list.get(i);

            if(current.getTcpFlags().get("FIN")) {
                partnerA=current.getIpSender();
                partnerB=current.getIpReceiver();
                seqA=current.getSeqNumber();

                List<PcapPacket> rest=getRestOfList(list,i);


            }
        }

        return result;
    }

    private static Pair checkForSecondPacketFinishing(List<PcapPacket> list, Long seq, InetAddress partnerB) {
        Long result=null;

        for(PcapPacket packet : list) {
            if(packet.getTcpFlags().get("ACK") && packet.getAckNumber()==(seq+1)
                && packet.getTcpFlags().get("FIN")) {
                result=packet.getSeqNumber();
                return new Pair(result,packet);
            }
        }

        return new Pair(result, null);
    }
}

