package net.astralounge.foundationapi.common.plugin.interfaces;

import com.google.gson.Gson;
import net.astralounge.foundationapi.common.component.FoundationComponent;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.AutoReloadableConfigurationHandler;
import net.astralounge.foundationapi.common.datamanagement.files.manager.FileManager;
import net.astralounge.foundationapi.common.dependencies.DependencyManager;
import net.astralounge.foundationapi.common.dependencies.PluginDependency;
import net.astralounge.foundationapi.common.internalmessaging.InternalMessageManager;
import net.astralounge.foundationapi.common.localisation.configuration.MessagesConfiguration;
import net.astralounge.foundationapi.common.localisation.managers.MessagesManager;
import net.astralounge.foundationapi.common.localisation.managers.PlaceholderManager;
import net.astralounge.foundationapi.common.manager.CommonManager;
import net.astralounge.foundationapi.common.plugin.configuration.GlobalConfiguration;
import net.astralounge.foundationapi.common.startup.StartupManager;
import net.astralounge.foundationapi.common.startup.StartupTask;
import net.astralounge.foundationapi.common.startup.configuration.StartupConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FoundationDefaultPlugin<T extends FoundationDefaultPlugin<T>> extends FoundationBasicPlugin {

    // Important Managers
    @NotNull FileManager getFileManager();

    @NotNull Gson getGson();

    @NotNull StartupManager<StartupConfiguration> getStartupManager();

    @NotNull InternalMessageManager getInternalMessageManager();

    @NotNull MessagesManager<MessagesConfiguration> getMessagesManager();

    @NotNull PlaceholderManager getPlaceholderManager();

    @NotNull DependencyManager getDependencyManager();

    // Configuration File

    @NotNull AutoReloadableConfigurationHandler<? extends GlobalConfiguration> getPluginConfiguration();

    default int getAutoReloadTime() {
        GlobalConfiguration configuration = getPluginConfiguration().getConfigurationObject();
        if (configuration != null) return configuration.autoReload.globalAutoReloadTime;
        return 10;
    }

    default boolean isAutoReloadEnabled() {
        GlobalConfiguration configuration = getPluginConfiguration().getConfigurationObject();
        if (configuration != null) return configuration.autoReload.globalAutoReloadEnabled;
        return false;
    }

    // Start-up Related Information
    @NotNull List<PluginDependency> getPluginDependencies();

    @NotNull List<CommonManager> getCommonManagers();

    default @Nullable <MANAGER extends CommonManager> MANAGER getCommonManager(Class<MANAGER> clazz) {
        return getCommonManagers().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }

    @NotNull List<CommonManager> getAdditionalManagers();

    @NotNull List<FoundationComponent<T>> getComponents();

    @NotNull List<StartupTask> startupTasks();

    // Plugin Start/Stop Functions
    void onPluginLoad() throws Throwable;

    void onPluginEnable() throws Throwable;

    void onPluginDisable() throws Throwable;

    void scheduleDisable();
}

