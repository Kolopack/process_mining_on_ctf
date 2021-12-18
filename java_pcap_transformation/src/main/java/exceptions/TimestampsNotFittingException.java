package exceptions;

/**
 * The type Timestamps not fitting exception.
 */
public class UnavailableException extends RuntimeException{
    private static final String errorMessage="The service you entered is not supported by this tool. Please check the spelling.";

    /**
     * Instantiates a new Timestamps not fitting exception.
     */
    public UnavailableException() {
        super(errorMessage);
    }
}
