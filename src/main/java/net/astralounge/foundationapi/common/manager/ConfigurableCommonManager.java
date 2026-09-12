package net.astralounge.foundationapi.common.manager;

import net.astralounge.foundationapi.common.datamanagement.files.configuration.AutoReloadableConfiguration;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.AutoReloadableConfigurationHandler;
import net.astralounge.foundationapi.common.datamanagement.files.manager.FileManager;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;

import java.util.Collection;
import java.util.List;

public abstract class ConfigurableCommonManager<T extends AutoReloadableConfiguration> extends CommonManager {

    private final AutoReloadableConfigurationHandler<T> configurationHandler;

    public ConfigurableCommonManager(FoundationDefaultPlugin<?> plugin, AutoReloadableConfigurationHandler<T> configurationHandler) {
        super(plugin);
        this.configurationHandler = configurationHandler;
    }

    @Override
    public Collection<Class<? extends CommonManager>> getCommonDependencies() {
        return List.of(FileManager.class);
    }

    public AutoReloadableConfigurationHandler<T> getConfigurationHandler() {
        return configurationHandler;
    }
}
