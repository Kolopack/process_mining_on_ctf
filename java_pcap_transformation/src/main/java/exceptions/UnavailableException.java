package exceptions;

/**
 * The type Unavailable exception.
 * Is thrown when the service submitted by the user is not supported by this implementation.
 * (So is not part of the implementation)
 */
public class UnavailableException extends RuntimeException{
    /**
     * Static error message
     */
    private static final String errorMessage="The service you entered is not supported by this tool. Please check the spelling.";

    /**
     * Instantiates a new Unavailable exception using the static error-message
     */
    public UnavailableException() {
        super(errorMessage);
    }
}
