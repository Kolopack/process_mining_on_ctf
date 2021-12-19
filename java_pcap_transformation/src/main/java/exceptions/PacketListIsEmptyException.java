package exceptions;

/**
 * Exception which is thrown when the list of filtered packets is empty - so there is no communication
 * inside of the PCAP-files between user-declared team and service
 */
public class PacketListIsEmptyException extends RuntimeException{
    /**
     * Static error message
     */
    private static final String errorMessage="The List of PcapPackets was empty - internal error.";

    /**
     * Instantiates a new Packet list is empty exception with defined static error-message
     */
    public PacketListIsEmptyException() {
        super(errorMessage);
    }
}
