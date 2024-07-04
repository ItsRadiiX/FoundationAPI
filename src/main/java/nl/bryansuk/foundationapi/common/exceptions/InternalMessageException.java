package nl.bryansuk.foundationapi.common.exceptions;


public class InternalMessageException extends RuntimeException {
    public InternalMessageException() {
    }

    public InternalMessageException(String message) {
        super(message);
    }

    public InternalMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalMessageException(Throwable cause) {
        super(cause);
    }

    public InternalMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
