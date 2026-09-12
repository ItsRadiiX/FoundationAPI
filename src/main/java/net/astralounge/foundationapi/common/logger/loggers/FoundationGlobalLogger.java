package net.astralounge.foundationapi.common.logger.loggers;

import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.AutoReloadableConfigurationHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.ConfigurationHandler;
import net.astralounge.foundationapi.common.logger.configuration.LoggerConfig;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public class FoundationGlobalLogger extends FoundationConfigurableLogger<LoggerConfig> {

    private final FoundationDefaultPlugin<?> plugin;

    public FoundationGlobalLogger(FoundationDefaultPlugin<?> plugin) {
        this.plugin = plugin;
    }

    @Override
    public void instantiateConfigurationHandler() {
        this.configurationHandler = new AutoReloadableConfigurationHandler<>(
                ConfigurationHandler.builder(plugin, LoggerConfig.class, "global-logger.conf")
                        .priority(101)
                        .instantEnable(true)
                        .logger(this),
                true);
        this.configurationHandler.write();
    }

    @Override
    public ComponentLogger setupComponentLogger() {
        return ComponentLogger.logger("FoundationGlobal");
    }

    @Override
    public boolean isManagerInDebug(String managerName) {
        return false;
    }
}
