package net.astralounge.foundationapi.paper.exceptions;

public class ItemManagerException extends Exception {

    public ItemManagerException() {}

    public ItemManagerException(String message) {
        super(message);
    }

    public ItemManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemManagerException(Throwable cause) {
        super(cause);
    }

    public ItemManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
