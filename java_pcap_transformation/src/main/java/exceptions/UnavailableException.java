package exceptions;

public class TimestampsNotFittingException extends Exception{
    private static final String errorMessage="The finish is not after the connection-establishing";

    public TimestampsNotFittingException() {
        super(errorMessage);
    }
}
