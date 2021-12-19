package exceptions;

/**
 * The type Timestamps not fitting exception.
 * Is thrown when the connection-establishing is not before a finishing
 */
public class TimestampsNotFittingException extends Exception{
    /**
     * Static error message
     */
    private static final String errorMessage="The finish is not after the connection-establishing";

    /**
     * Instantiates a new Timestamps-not-fitting exception, using the static error-message
     */
    public TimestampsNotFittingException() {
        super(errorMessage);
    }
}
