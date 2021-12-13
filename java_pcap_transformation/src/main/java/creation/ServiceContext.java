package creation;

import exceptions.TimestampsNotFittingException;

import java.net.InetAddress;

public class ServiceContext {
    private static final String MOSTWANTED="Mostwanted";
    private static final String OVERCOVERT="Overcovert";
    private static final String FLAGSUBMISSION="Flagsubmission";

    public static IService createServiceClass(String serviceName, String teamName, InetAddress teamIP,
                                              String teamMask) {
        IService result;

        if(serviceName.equals(MOSTWANTED)) {
            result=new MostwantedService(teamName, teamIP, teamMask);
            return result;
        }
        if(serviceName.equals(OVERCOVERT)) {
            result=new OvercovertService(teamName, teamIP, teamMask);
            return result;
        }
        if(serviceName.equals(FLAGSUBMISSION)) {
            result=new FlagsubmissionService(teamName,teamIP,teamMask);
            return result;
        }
        throw new TimestampsNotFittingException();
    }
}
