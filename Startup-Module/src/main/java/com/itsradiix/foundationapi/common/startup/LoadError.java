package com.itsradiix.foundationapi.common.startup;

public record LoadError(LoadError.Level level, String message) {

    public enum Level {
        RISK,
        CRITICAL,
        FATAL
    }
}
