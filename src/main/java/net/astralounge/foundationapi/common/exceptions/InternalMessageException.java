package net.astralounge.foundationapi.common.exceptions;

public class InternalMessageException extends Exception {
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
