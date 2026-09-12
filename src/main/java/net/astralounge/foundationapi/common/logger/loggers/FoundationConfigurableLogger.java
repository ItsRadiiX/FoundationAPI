package net.astralounge.foundationapi.common.logger.loggers;

import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.AutoReloadableConfigurationHandler;
import net.astralounge.foundationapi.common.logger.configuration.LoggerConfig;
import net.astralounge.foundationapi.common.logger.enums.LogLevel;

public abstract class FoundationConfigurableLogger<TConfig extends LoggerConfig> extends FoundationLogger {

    protected AutoReloadableConfigurationHandler<TConfig> configurationHandler;

    public FoundationConfigurableLogger() {}

    public abstract void instantiateConfigurationHandler();

    @Override
    public LogLevel getLogLevel() {
        if (this.configurationHandler == null) return LogLevel.INFO;
        return this.configurationHandler.getConfigurationObject().logLevel;
    }

    public AutoReloadableConfigurationHandler<TConfig> getConfigurationHandler() {
        return configurationHandler;
    }

    /* =========================
       UTILITY
       ========================= */

    public boolean isDebugEnabled() {
        return getLogLevel() == LogLevel.DEBUG;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.configurationHandler.getConfigurationObject().logLevel = logLevel;
    }
}
