package enumerations;

/**
 * The enum Mostwanted part.
 * For labelling the different sections of a typical Mostwanted-process
 */
public enum MostwantedPart {
    /**
     * TCP-Three-Way-Handshake
     */
    HANDSHAKE,
    /**
     * Part of PSHACK (inbetween)
     */
    PSHACK,
    /**
     * TCP-finishing process
     */
    FINISHING
}
