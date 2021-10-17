package creation;

import exceptions.TimestampsNotFittingException;

import java.net.InetAddress;

public class ServiceContext {
    private static final String MOSTWANTED="Mostwanted";
    private static final String OVERCOVERT="Overcovert";

    public static IService createServiceClass(String serviceName, String teamName, InetAddress teamIP) {
        IService result;

        if(serviceName.equals(MOSTWANTED)) {
            result=new MostwantedService(teamName, teamIP);
            return result;
        }
        if(serviceName.equals(OVERCOVERT)) {
            result=new OvercovertService(teamName, teamIP);
            return result;
        }
        throw new TimestampsNotFittingException();
    }
}
