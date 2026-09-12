package net.astralounge.foundationapi.common.plugin.interfaces;

import net.astralounge.foundationapi.common.logger.loggers.FoundationPluginLogger;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;

public interface FoundationBasicPlugin {

    @NotNull String getPluginName();

    @NotNull String getPluginVersion();

    @NotNull String getPluginAuthor();

    @NotNull String getMinecraftVersion();

    // Logger
    @NotNull FoundationPluginLogger getFoundationPluginLogger();

    @NotNull ComponentLogger getComponentLogger();

    // FileManagement Information
    @Nullable InputStream getDefaultResource(String path);

    @NotNull File getDataFolder();
}
