package net.astralounge.foundationapi.common.logger.loggers;

import net.astralounge.foundationapi.common.logger.enums.LogLevel;
import net.astralounge.foundationapi.common.manager.CommonManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

/**
 * Base logger abstraction with lazy ComponentLogger initialisation.
 *
 * Subclasses must implement {@link #setupComponentLogger()}, but this method
 * is never called from the constructor anymore (to avoid calling overridable
 * methods before subclass state is initialised).
 */
public abstract class FoundationLogger {

    private ComponentLogger logger;

    protected FoundationLogger() {
        // No calls to overridable methods here.
    }

    /**
     * Lazily initialise the ComponentLogger on first access.
     */
    public ComponentLogger getComponentLogger() {
        if (logger == null) {
            logger = setupComponentLogger();
        }
        return logger;
    }

    protected abstract ComponentLogger setupComponentLogger();
    public abstract LogLevel getLogLevel();
    public abstract void setLogLevel(LogLevel logLevel);
    public abstract boolean isManagerInDebug(String managerName);

    /* =========================
       INFO
       ========================= */

    public void info(String message) {
        getComponentLogger().info(message);
    }

    public void info(String format, Object arg) {
        getComponentLogger().info(format, arg);
    }

    public void info(String format, Object arg1, Object arg2) {
        getComponentLogger().info(format, arg1, arg2);
    }

    public void info(String format, Object... arguments) {
        getComponentLogger().info(format, arguments);
    }

    public void info(Component component) {
        getComponentLogger().info(component);
    }

    public void info(Component component, Object arg) {
        getComponentLogger().info(component, arg);
    }

    public void info(Component component, Object arg1, Object arg2) {
        getComponentLogger().info(component, arg1, arg2);
    }

    public void info(Component component, Object... arguments) {
        getComponentLogger().info(component, arguments);
    }

    /* =========================
       DEBUG
       ========================= */

    public void startupReport(boolean startupReport, Component message) {
        if (startupReport) getComponentLogger().info(message);
    }

    public void debug(String message) {
        if (getLogLevel() == LogLevel.DEBUG) {
            getComponentLogger().info(Component.text(message, NamedTextColor.AQUA));
        }
    }

    public void debug(String format, Object arg) {
        if (getLogLevel() == LogLevel.DEBUG) {
            getComponentLogger().info(Component.text(format, NamedTextColor.AQUA), arg);
        }
    }

    public void debug(String format, Object arg1, Object arg2) {
        if (getLogLevel() == LogLevel.DEBUG) {
            getComponentLogger().info(Component.text(format, NamedTextColor.AQUA), arg1, arg2);
        }
    }

    public void debug(String format, Object... arguments) {
        if (getLogLevel() == LogLevel.DEBUG) {
            getComponentLogger().info(Component.text(format, NamedTextColor.AQUA), arguments);
        }
    }

    public void debug(Component component) {
        if (getLogLevel() == LogLevel.DEBUG) {
            getComponentLogger().info(component.color(NamedTextColor.AQUA));
        }
    }

    public void debug(Component component, Object arg) {
        if (getLogLevel() == LogLevel.DEBUG) {
            getComponentLogger().info(component.color(NamedTextColor.AQUA), arg);
        }
    }

    public void debug(Component component, Object arg1, Object arg2) {
        if (getLogLevel() == LogLevel.DEBUG) {
            getComponentLogger().info(component.color(NamedTextColor.AQUA), arg1, arg2);
        }
    }

    public void debug(Component component, Object... arguments) {
        if (getLogLevel() == LogLevel.DEBUG) {
            getComponentLogger().info(component.color(NamedTextColor.AQUA), arguments);
        }
    }

    /* =========================
       WARN (uses logger.info)
       ========================= */

    public void warn(String message) {
        getComponentLogger().info(Component.text(message, NamedTextColor.RED));
    }

    public void warn(String format, Object arg) {
        getComponentLogger().info(Component.text(format, NamedTextColor.RED), arg);
    }

    public void warn(String format, Object arg1, Object arg2) {
        getComponentLogger().info(Component.text(format, NamedTextColor.RED), arg1, arg2);
    }

    public void warn(String format, Object... arguments) {
        getComponentLogger().info(Component.text(format, NamedTextColor.RED), arguments);
    }

    public void warn(Component component) {
        getComponentLogger().info(component.color(NamedTextColor.RED));
    }

    public void warn(Component component, Object arg) {
        getComponentLogger().info(component.color(NamedTextColor.RED), arg);
    }

    public void warn(Component component, Object arg1, Object arg2) {
        getComponentLogger().info(component.color(NamedTextColor.RED), arg1, arg2);
    }

    public void warn(Component component, Object... arguments) {
        getComponentLogger().info(component.color(NamedTextColor.RED), arguments);
    }

    /* =========================
       ERROR (uses logger.info)
       ========================= */

    public void error(String message) {
        getComponentLogger().info(Component.text(message, NamedTextColor.DARK_RED));
    }

    public void error(String format, Object arg) {
        getComponentLogger().info(Component.text(format, NamedTextColor.DARK_RED), arg);
    }

    public void error(String format, Object arg1, Object arg2) {
        getComponentLogger().info(Component.text(format, NamedTextColor.DARK_RED), arg1, arg2);
    }

    public void error(String format, Object... arguments) {
        getComponentLogger().info(Component.text(format, NamedTextColor.DARK_RED), arguments);
    }

    public void error(Component component) {
        getComponentLogger().info(component.color(NamedTextColor.DARK_RED));
    }

    public void error(Component component, Object arg) {
        getComponentLogger().info(component.color(NamedTextColor.DARK_RED), arg);
    }

    public void error(Component component, Object arg1, Object arg2) {
        getComponentLogger().info(component.color(NamedTextColor.DARK_RED), arg1, arg2);
    }

    public void error(Component component, Object... arguments) {
        getComponentLogger().info(component.color(NamedTextColor.DARK_RED), arguments);
    }

    public void error(Throwable throwable) {
        getComponentLogger().info(Component.text(throwable.getMessage(), NamedTextColor.DARK_RED), throwable);
    }

    public void error(String message, Throwable throwable) {
        getComponentLogger().info(Component.text(message, NamedTextColor.DARK_RED), throwable);
    }

    public void error(Component component, Throwable throwable) {
        getComponentLogger().info(component.color(NamedTextColor.DARK_RED), throwable);
    }

    // ----- DEBUG FOR MANAGERS ----- //

    public void debug(CommonManager manager, String message) {
        if (getLogLevel() == LogLevel.DEBUG || isManagerInDebug(manager.getClass().getSimpleName())) {
            info(Component.text(message, NamedTextColor.AQUA));
        }
    }

    public void debug(CommonManager manager, String format, Object arg) {
        if (getLogLevel() == LogLevel.DEBUG || isManagerInDebug(manager.getClass().getSimpleName())) {
            info(Component.text(format, NamedTextColor.AQUA), arg);
        }
    }

    public void debug(CommonManager manager, String format, Object arg1, Object arg2) {
        if (getLogLevel() == LogLevel.DEBUG || isManagerInDebug(manager.getClass().getSimpleName())) {
            info(Component.text(format, NamedTextColor.AQUA), arg1, arg2);
        }
    }

    public void debug(CommonManager manager, String format, Object... arguments) {
        if (getLogLevel() == LogLevel.DEBUG || isManagerInDebug(manager.getClass().getSimpleName())) {
            info(Component.text(format, NamedTextColor.AQUA), arguments);
        }
    }

    public void debug(CommonManager manager, Component component) {
        if (getLogLevel() == LogLevel.DEBUG || isManagerInDebug(manager.getClass().getSimpleName())) {
            info(component.color(NamedTextColor.AQUA));
        }
    }

    public void debug(CommonManager manager, Component component, Object arg) {
        if (getLogLevel() == LogLevel.DEBUG || isManagerInDebug(manager.getClass().getSimpleName())) {
            info(component.color(NamedTextColor.AQUA), arg);
        }
    }

    public void debug(CommonManager manager, Component component, Object arg1, Object arg2) {
        if (getLogLevel() == LogLevel.DEBUG || isManagerInDebug(manager.getClass().getSimpleName())) {
            info(component.color(NamedTextColor.AQUA), arg1, arg2);
        }
    }

    public void debug(CommonManager manager, Component component, Object... arguments) {
        if (getLogLevel() == LogLevel.DEBUG || isManagerInDebug(manager.getClass().getSimpleName())) {
            info(component.color(NamedTextColor.AQUA), arguments);
        }
    }

}
