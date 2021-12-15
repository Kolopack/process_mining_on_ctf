package constants;

/**
 * Class including static String-constants containing the different HTTP REST-types as String-names.
 * Used for filtering out the methods inside of packet-payloads.
 */
public class HTTPConstants {
    /**
     * GET: for receiving data (for instance the webpage or only the favicon)
     */
    public static final String GET="GET";
    /**
     * POST: for submitting data vie HTTP (f.i. a flag-string)
     */
    public static final String POST="POST";
    /**
     * The Protocol HTTP as a String for filtering out HTTP-related data of the package-payload
     */
    public static final String HTTP="HTTP";
}
