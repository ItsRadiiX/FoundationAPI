package nl.bryansuk.foundationapi.common.exceptions;

public class MenuManagerNotSetupException extends RuntimeException {

    public MenuManagerNotSetupException() {}

    public MenuManagerNotSetupException(String message) {
        super(message);
    }

    public MenuManagerNotSetupException(String message, Throwable cause) {
        super(message, cause);
    }

    public MenuManagerNotSetupException(Throwable cause) {
        super(cause);
    }

    public MenuManagerNotSetupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
