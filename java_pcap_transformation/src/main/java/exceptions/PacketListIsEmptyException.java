package exceptions;

public class PacketListIsEmptyException extends RuntimeException{
    private static final String errorMessage="The List of PcapPackets was empty - internal error.";

    public PacketListIsEmptyException() {
        super(errorMessage);
    }
}
