package net.astralounge.foundationapi.common.logger.configuration;

import net.astralounge.foundationapi.common.datamanagement.files.configuration.AutoReloadableConfiguration;
import net.astralounge.foundationapi.common.logger.enums.LogLevel;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class LoggerConfig extends AutoReloadableConfiguration {

    @Comment("""
            The log level to use.
            This value defaults to INFO.
            Options: INFO or DEBUG""")
    public LogLevel logLevel;

    public LoggerConfig() {
        logLevel = LogLevel.INFO;
    }
}
