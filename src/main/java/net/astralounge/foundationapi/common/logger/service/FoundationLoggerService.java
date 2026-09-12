package net.astralounge.foundationapi.common.logger.service;

import net.astralounge.foundationapi.common.logger.loggers.FoundationGlobalLogger;
import net.astralounge.foundationapi.common.logger.loggers.FoundationLogger;
import net.astralounge.foundationapi.common.logger.loggers.FoundationPluginLogger;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry/chooser for Foundation loggers.
 * <p>
 * Responsibilities:
 * - Holds a single global logger instance (FoundationGlobalLogger).
 * - Holds per-plugin logger instances (FoundationPluginLogger) keyed by FoundationDefaultPlugin.
 * - Provides a unified way to get the "right" logger:
 *   - plugin logger when a plugin is provided
 *   - Global logger otherwise or when desired.
 */
public final class FoundationLoggerService {

    private static final Map<FoundationDefaultPlugin<?>, FoundationPluginLogger> PLUGIN_LOGGERS =
            new ConcurrentHashMap<>();

    private static volatile FoundationGlobalLogger GLOBAL_LOGGER;
    private static boolean setup;

    private FoundationLoggerService() {
        // utility class
    }

    /* =========================
       GLOBAL LOGGER MANAGEMENT
       ========================= */

    /**
     * Initialize the global logger using the given plugin to:
     * - resolve configuration (global-logger.conf)
     * - obtain a ComponentLogger
     * <p>
     * You typically call this once during FoundationAPI bootstrap
     * (e.g. from your core/Foundation plugin).
     */
    public static void initGlobalLogger(@NotNull FoundationDefaultPlugin<?> plugin) {
        GLOBAL_LOGGER = new FoundationGlobalLogger(plugin);
    }

    public static void initGlobalConfigHandler() {
        GLOBAL_LOGGER.instantiateConfigurationHandler();
        GLOBAL_LOGGER.debug("FoundationGlobalLogger initialized.");
        setupComplete();
    }

    /**
     * Get the global logger, creating a minimal one if not initialized.
     * If you care about configuration, you should call one of the initGlobal(...) methods first.
     */
    public static @NotNull FoundationGlobalLogger getGlobalLogger() {
        return GLOBAL_LOGGER;
    }

    /* =========================
       PLUGIN LOGGER MANAGEMENT
       ========================= */

    /**
     * Get (or create) the logger for a specific plugin.
     */
    public static @NotNull FoundationPluginLogger getPluginLogger(@NotNull FoundationDefaultPlugin<?> plugin) {
        return PLUGIN_LOGGERS.computeIfAbsent(plugin, FoundationPluginLogger::new);
    }

    /**
     * Remove a plugin logger from the registry (e.g. when a plugin is disabled).
     */
    public static void unregister(@NotNull FoundationDefaultPlugin<?> plugin) {
        PLUGIN_LOGGERS.remove(plugin);
    }

    /* =========================
       UNIFIED ACCESS
       ========================= */

    /**
     * Unified accessor:
     * - If plugin is non-null, returns its FoundationPluginLogger (creating it if missing).
     * - If plugin is null, returns the global logger.
     * <p>
     * Never returns null.
     */
    public static @NotNull FoundationLogger get(@Nullable FoundationDefaultPlugin<?> plugin) {
        if (!setup) {
            return getGlobalLogger();
        }

        if (plugin == null) {
            return getGlobalLogger();
        }

        return getPluginLogger(plugin);
    }

    private static void setupComplete() {
        setup = true;
    }

    public static void unregisterSelf() {
        setup = false;
    }
}
