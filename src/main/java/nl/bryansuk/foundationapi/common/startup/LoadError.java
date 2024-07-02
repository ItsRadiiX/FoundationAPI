package nl.bryansuk.foundationapi.common.startup;

public record LoadError(nl.bryansuk.foundationapi.common.startup.LoadError.Level level, String message) {

    public enum Level {
        RISK,
        CRITICAL,
        FATAL
    }
}
