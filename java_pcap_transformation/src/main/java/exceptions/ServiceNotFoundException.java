package exceptions;

public class ServiceNotFoundException extends RuntimeException{
    private static final String errorMessage="The service you entered is not supported by this tool. Please check the spelling.";

    public ServiceNotFoundException() {
        super(errorMessage);
    }
}
