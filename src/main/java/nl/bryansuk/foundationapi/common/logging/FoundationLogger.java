package nl.bryansuk.foundationapi.common.logging;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import nl.bryansuk.foundationapi.common.startup.PluginStartupData;
import nl.bryansuk.foundationapi.common.textmanager.TextCreator;

public class FoundationLogger {
    private final ComponentLogger logger;
    private PluginStartupData startupData;

    public FoundationLogger(final ComponentLogger logger) {
        this.logger = logger;
    }

    /**
     * Log some information to the console.
     * @param message the information to be logged.
     */
    public void info(String message){
        logger.info(TextCreator.create(message));
    }

    public void info(String message, Object... arguments){
        logger.info(TextCreator.create(formatMessage(message, arguments)));
    }

    public void warn(String warning){
        logger.warn(TextCreator.create(warning));
    }

    public void warn(String warning, Object... arguments){
        logger.warn(TextCreator.create(formatMessage(warning, arguments)));
    }

    public void debug(String message){
        logger.debug(TextCreator.create(message));
    }

    public void debug(String message, Object... arguments){
        logger.debug(TextCreator.create(formatMessage(message, arguments)));
    }

    /**
     * Logs an error to the console.
     * @param error the error to be logged.
     */
    public void error(String error){
        logger.error(TextCreator.create(error));
    }

    /**
     * Logs an error to the console.
     * @param error the error to be logged.
     */
    public void error(String error, Exception exception){
        error(error);
        error(exception);
    }

    /**
     * Logs an error to the console.
     * @param error the error to be logged.
     */
    public void error(Exception exception, String error, Object... arguments){
        error(formatMessage(error, arguments));
        error(exception);
    }

    /**
     * Logs an error to the console.
     * @param exception the error to be logged.
     */
    public void error(Exception exception){
        logger.error(TextCreator.create(exception.getMessage()));
    }

    public void setStartupData(PluginStartupData startupData) {
        this.startupData = startupData;
    }

    public void startupDebug(String message){
        if (startupData.getStartupDebug()) info(message);
    }

    public ComponentLogger getLogger() {
        return logger;
    }

    private static String formatMessage(String messageTemplate, Object... args) {
        if (messageTemplate == null) return "Null";
        if (args.length == 1) {
            if (messageTemplate.contains("{}")) return messageTemplate.replace("{}", String.valueOf(args[0]));
            if (messageTemplate.contains("{0}")) return messageTemplate.replace("{0}", String.valueOf(args[0]));
        }

        for (int i = 0; i < args.length; i++) {
            messageTemplate = messageTemplate.replace("{" + i + "}", args[i].toString());
        }
        return messageTemplate;
    }
}
