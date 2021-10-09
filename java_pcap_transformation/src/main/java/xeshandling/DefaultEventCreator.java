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
        Long ackA;
        InetAddress partnerA;
        InetAddress partnerB;

        for(int i=0; i<list.size();++i) {
            PcapPacket current=list.get(i);

            if(current.getTcpFlags().get("FIN")) {
                List<PcapPacket> finish=new ArrayList<>();

                partnerA=current.getIpSender();
                partnerB=current.getIpReceiver();
                seqA=current.getSeqNumber();
                ackA=current.getAckNumber();


                List<PcapPacket> rest=getRestOfList(list,i);

                List<Pair<Long, PcapPacket>> secondPacket= checkForSecondPacketFinishing(rest,seqA, ackA,partnerB);
                if(!secondPacket.isEmpty()) {
                    finish.add(current);
                    for(Pair<Long, PcapPacket> pair : secondPacket) {
                        finish.add(pair.getValue());
                    }

                    Pair<Boolean, PcapPacket> thirdPacket= checkForThirdPacketFinishing(rest,secondPacket.get(0).getKey(),partnerA);
                    if(thirdPacket.getKey()) {
                        finish.add(thirdPacket.getValue());
                    }
                }
                if(!finish.isEmpty()){
                    result.add(finish);
                }
            }
        }

        return result;
    }

    private static List<Pair<Long, PcapPacket>> checkForSecondPacketFinishing(List<PcapPacket> list, Long seq, Long ack, InetAddress partnerB) {
        Long result=null;
        List<Pair<Long, PcapPacket>> resultList=new ArrayList<>();

        for(PcapPacket packet : list) {
            if(packet.getIpSender().equals(partnerB) && packet.getTcpFlags().get("ACK")
                    && packet.getAckNumber()==(seq+1) && packet.getSeqNumber()==ack) {
                result=packet.getSeqNumber();
                resultList.add(new Pair(result,packet));
                return resultList;
                /*if(packet.getTcpFlags().get("FIN")) {
                    return resultList;
                }
                else {
                    Pair separatePacket=checkForSeparateFINpacket(list,seq, ack, partnerB);
                    resultList.add(separatePacket);
                    return resultList;
                }*/

            }

        }
        return resultList;
    }

    /*private static Pair checkForSeparateFINpacket(List<PcapPacket> list, Long seq, Long ack, InetAddress partnerB) {
        Long result=null;

        //Second case: ACK and FIN are not sent in the same packet
        for(PcapPacket packet: list) {
            if( packet.getIpSender().equals(partnerB) && packet.getTcpFlags().get("ACK")
                    && packet.getAckNumber()==(seq+1) && !packet.getTcpFlags().get("FIN")
                    && packet.getSeqNumber()==ack) {
                    result=packet.getSeqNumber();
                    return new Pair(result,packet);
            }
        }
        return new Pair(result,null);
    }*/

    private static Pair checkForThirdPacketFinishing(List<PcapPacket> list, Long seqB, InetAddress partnerA) {
        boolean result=false;

        for(PcapPacket packet : list) {
            if(packet.getIpSender().equals(partnerA) && packet.getTcpFlags().get("ACK")
                && packet.getAckNumber()==(seqB+1)) {
                result=true;
                return new Pair(result,packet);
            }
        }
        return new Pair(result,null);
    }

    public static List<PcapPacket> getPSHACKExchangingBetween(List<PcapPacket> list, InetAddress partnerA, InetAddress partnerB) {

    }
}

