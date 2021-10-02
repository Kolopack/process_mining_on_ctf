package creation;

import exceptions.ServiceNotFoundException;
import packets.PcapPacket;

import java.util.List;

public class ServiceContext {
    private static final String MOSTWANTED="Mostwanted";
    private static final String OVERCOVERT="Overcovert";

    public static IService createServiceClass(String serviceName, String teamName) {
        IService result;

        if(serviceName.equals(MOSTWANTED)) {
            result=new MostwantedService(teamName);
            return result;
        }
        if(serviceName.equals(OVERCOVERT)) {
            result=new OvercovertService(teamName);
            return result;
        }
        throw new ServiceNotFoundException();
    }
}
