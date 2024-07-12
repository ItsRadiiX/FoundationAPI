package com.itsradiix.foundationapi.paper.exceptions;

public class MenuManagerException extends RuntimeException {

    public MenuManagerException() {}

    public MenuManagerException(String message) {
        super(message);
    }

    public MenuManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MenuManagerException(Throwable cause) {
        super(cause);
    }

    public MenuManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
