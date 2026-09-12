package net.astralounge.foundationapi.common.logger.configuration;

import net.astralounge.foundationapi.common.logger.enums.LogLevel;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class PluginLoggerConfig extends LoggerConfig {

    @Comment("""
            The log levels to use for specific managers.
            Each value defaults to INFO.
            Options: INFO or DEBUG""")
    public Map<String, LogLevel> managerLogLevels;

    @Comment("""
            Enable to log when no file changes are detected.
            Can spam your console, treat with care.""")
    public boolean debugNoFileChanges;

    public PluginLoggerConfig() {
        managerLogLevels = new HashMap<>();
        debugNoFileChanges = false;
    }

}
