package exceptions;

/**
 * Exception which is thrown when the program identified a HTTP-method, but could not indicate further,
 * which method (GET, POST,..)
 */
public class NoMethodFoundException extends RuntimeException{
    private static final String errorMessage="Although made as HTTP-request, no HTTP-request-method was extractable.";

    /**
     * Instantiates a new No method found exception with defined static error-message
     */
    public NoMethodFoundException() {
        super(errorMessage);
    }
}
