package enumerations;

/**
 * The enum Handshakes.
 * For labelling the different parts of the TCP-Three-Way-Handshake
 */
public enum Handshakes {
    /**
     * First part (SYN)
     */
    FIRST,
    /**
     * Second part (ACK, SYN)
     */
    SECOND,
    /**
     * Third part (ACK)
     */
    THIRD
}
