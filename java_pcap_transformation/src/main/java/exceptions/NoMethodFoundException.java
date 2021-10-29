package exceptions;

public class NoMethodFoundException extends RuntimeException{
    private static final String errorMessage="Although made as HTTP-request, no HTTP-request-method was extractable.";

    public NoMethodFoundException() {
        super(errorMessage);
    }
}
