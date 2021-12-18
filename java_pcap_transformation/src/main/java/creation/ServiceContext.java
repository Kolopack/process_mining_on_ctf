package creation;

import exceptions.TimestampsNotFittingException;

import java.net.InetAddress;

/**
 * The type Service context.
 */
public class ServiceContext {
    private static final String MOSTWANTED="Mostwanted";
    private static final String OVERCOVERT="Overcovert";
    private static final String FLAGSUBMISSION="Flagsubmission";

    /**
     * Create service class service. For checking which Service is analysed.
     *
     * @param serviceName the service name
     * @param teamName    the team name
     * @param teamIP      the team ip
     * @param teamMask    the team mask
     * @return the service which is currently analysed
     */
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
