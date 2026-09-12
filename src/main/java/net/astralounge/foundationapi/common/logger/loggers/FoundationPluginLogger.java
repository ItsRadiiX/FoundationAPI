package net.astralounge.foundationapi.common.logger.loggers;

import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.AutoReloadableConfigurationHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.ConfigurationHandler;
import net.astralounge.foundationapi.common.logger.configuration.PluginLoggerConfig;
import net.astralounge.foundationapi.common.logger.enums.LogLevel;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.util.List;
import java.util.Map;

public class FoundationPluginLogger extends FoundationConfigurableLogger<PluginLoggerConfig> {

    private final FoundationDefaultPlugin<?> plugin;

    public FoundationPluginLogger(FoundationDefaultPlugin<?> plugin) {
        this.plugin = plugin;
    }

    @Override
    public ComponentLogger setupComponentLogger() {
        return plugin.getComponentLogger();
    }

    @Override
    public void instantiateConfigurationHandler() {
        this.configurationHandler = new AutoReloadableConfigurationHandler<>(
                ConfigurationHandler.builder(plugin, PluginLoggerConfig.class, "logger.conf")
                        .priority(100)
                        .instantEnable(true), true);
    }

    /* =========================
       COMMON MANAGERS HANDLING
       ========================= */

    public boolean isManagerInDebug(String Manager) {
        return getLogLevelForManager(Manager) == LogLevel.DEBUG;
    }

    public void populateCommonManagers() {
        Map<String, LogLevel> map = configurationHandler.getConfigurationObject().managerLogLevels;
        List<String> managers = plugin.getCommonManagers()
                .stream()
                .map(commonManager -> commonManager.getClass().getSimpleName())
                .toList();
        for (String manager : managers) {
            if (!map.containsKey(manager)) {
                map.put(manager, LogLevel.INFO);
            }
        }

        configurationHandler.write();
    }

    public boolean setLogLevel(LogLevel logLevel, String Manager) {
        if (this.configurationHandler == null) return false;
        if (this.configurationHandler.getConfigurationObject().managerLogLevels == null) return false;
        this.configurationHandler.getConfigurationObject().managerLogLevels.put(Manager, logLevel);
        return true;
    }

    public LogLevel getLogLevelForManager(String Manager) {
        if (this.configurationHandler == null) return LogLevel.DEBUG;
        if (this.configurationHandler.getConfigurationObject().managerLogLevels == null) return LogLevel.DEBUG;
        if (!this.configurationHandler.getConfigurationObject().managerLogLevels.containsKey(Manager)) return LogLevel.DEBUG;
        return this.configurationHandler.getConfigurationObject().managerLogLevels.get(Manager);
    }

    public boolean getDebugNoFileChanges() {
        if (this.configurationHandler == null) return false;
        return this.configurationHandler.getConfigurationObject().debugNoFileChanges;
    }

}
