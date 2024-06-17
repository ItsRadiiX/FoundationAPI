package nl.bryansuk.foundationapi.logging;

import nl.bryansuk.foundationapi.plugin.FoundationPlugin;
import nl.bryansuk.foundationapi.textmanager.TextCreator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class FoundationLogger {

    private final Logger logger;
    private final FoundationPlugin plugin;

    public FoundationLogger(final Logger logger, final FoundationPlugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
    }

    /**
     * Log some information to the console.
     * @param message the information to be logged.
     */
    public void logToConsole(String message){
        logToConsole(Level.INFO, message);
    }

    /**
     * Logs an error to the console.
     * @param error the error to be logged.
     */
    public void errorToConsole(String error){
        logToConsole(Level.ERROR, error);
    }

    /**
     * Logs an error to the console.
     * @param exception the error to be logged.
     */
    public void errorToConsole(Exception exception){
        logToConsole(Level.ERROR, exception.getMessage());
    }

    /**
     * Logs an error to the console.
     * @param error the error to be logged.
     */
    public void errorToConsole(String error, Exception e){
        logToConsole(Level.ERROR, error, e);
    }

    public void logToConsole(Level level, String message){
        logger.log(level, TextCreator.translateColorCodes('&', message).content());
    }

    public void logToConsole(Level level, String message, Exception e){
        logger.log(level, TextCreator.translateColorCodes('&', message).content(), e);
    }

    public void startupDebug(String message){
        startupDebug(Level.INFO, message);
    }
    public void startupDebug(Level level, String message){
        if (plugin.getStartupData().getStartupDebug()) logToConsole(level, message);
    }

    public void startupDebug(Level level, String message, Exception e){
        if (plugin.getStartupData().getStartupDebug()) logToConsole(level, message, e);
    }

    public Logger getLogger() {
        return logger;
    }
}
