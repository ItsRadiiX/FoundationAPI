package net.astralounge.foundationapi.common.exceptions;

public class CircularDependencyException extends RuntimeException {
    public CircularDependencyException(String message) {
        super(message);
    }
}
