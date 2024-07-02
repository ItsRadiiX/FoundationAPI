package nl.bryansuk.foundationapi.common.exceptions;

public class FileManagerException extends RuntimeException {
    public FileManagerException() {
    }

    public FileManagerException(String message) {
        super(message);
    }

    public FileManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileManagerException(Throwable cause) {
        super(cause);
    }

    public FileManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
