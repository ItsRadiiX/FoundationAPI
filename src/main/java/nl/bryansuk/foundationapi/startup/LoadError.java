package nl.bryansuk.foundationapi.startup;

public class LoadError {

    private final Level level;
    private final String message;

    public LoadError(Level level, String message) {
        this.level = level;
        this.message = message;
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public enum Level{
        RISK,
        CRITICAL,
        FATAL
    }
}
