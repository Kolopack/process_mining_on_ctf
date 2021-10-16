package exceptions;

public class UnavailableException extends Exception{
    private static final String errorMessage="The finish is not after the connection-establishing";

    public UnavailableException() {
        super(errorMessage);
    }
}
