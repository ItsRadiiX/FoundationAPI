package net.astralounge.foundationapi.paper.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.astralounge.foundationapi.common.component.FoundationComponent;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.AutoReloadableConfigurationHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.ConfigurationHandler;
import net.astralounge.foundationapi.common.datamanagement.files.manager.FileManager;
import net.astralounge.foundationapi.common.dependencies.DependencyManager;
import net.astralounge.foundationapi.common.internalmessaging.InternalMessageManager;
import net.astralounge.foundationapi.common.localisation.LocalisationService;
import net.astralounge.foundationapi.common.localisation.configuration.MessagesConfiguration;
import net.astralounge.foundationapi.common.localisation.managers.MessagesManager;
import net.astralounge.foundationapi.common.localisation.managers.PlaceholderManager;
import net.astralounge.foundationapi.common.logger.loggers.FoundationPluginLogger;
import net.astralounge.foundationapi.common.logger.service.FoundationLoggerService;
import net.astralounge.foundationapi.common.manager.CommonManager;
import net.astralounge.foundationapi.common.manager.CommonManagerLoader;
import net.astralounge.foundationapi.common.plugin.configuration.GlobalConfiguration;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import net.astralounge.foundationapi.common.startup.StartupManager;
import net.astralounge.foundationapi.common.startup.StartupTask;
import net.astralounge.foundationapi.common.startup.configuration.StartupConfiguration;
import net.astralounge.foundationapi.paper.itemmanager.ItemManager;
import net.astralounge.foundationapi.paper.menumanager.v1.MenuManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.List;

@SuppressWarnings("unused")
public abstract class FoundationPaperPlugin<T extends FoundationPaperPlugin<T, GLOBAL_CONFIGURATION>,
        GLOBAL_CONFIGURATION extends GlobalConfiguration>
        extends JavaPlugin
        implements FoundationDefaultPlugin<T> {

    /* ---------------------------------------------------------------------
     * Core
     * --------------------------------------------------------------------- */

    protected T instance;
    private FoundationPaperServer foundationPaperServer;
    private FoundationPluginLogger foundationLogger;
    private CommonManagerLoader commonManagerLoader;

    /* ---------------------------------------------------------------------
     * Managers (core, per-plugin)
     * --------------------------------------------------------------------- */

    private FileManager fileManager;
    private StartupManager<StartupConfiguration> startupManager;
    private InternalMessageManager internalMessageManager;
    private DependencyManager dependencyManager;
    private MenuManager<T> menuManager;
    private ItemManager<T> itemManager;

    protected AutoReloadableConfigurationHandler<GLOBAL_CONFIGURATION> configurationHandler;
    protected Class<GLOBAL_CONFIGURATION> configurationClass;
    protected AutoReloadableConfigurationHandler<StartupConfiguration> startupConfigurationHandler;
    protected AutoReloadableConfigurationHandler<MessagesConfiguration> messagesConfiguration;

    /* ---------------------------------------------------------------------
     * Runtime
     * --------------------------------------------------------------------- */

    protected long startTime;

    /* ---------------------------------------------------------------------
     * Bukkit Lifecycle
     * --------------------------------------------------------------------- */

    boolean isLoaded = false;

    protected abstract Class<GLOBAL_CONFIGURATION> getGlobalConfigurationClass();

    @Override
    public void onLoad() {
        // safe: this cast pattern is standard in abstract plugin bases
        instance = (T) this;
        foundationPaperServer = new FoundationPaperServer(getServer());

        try {
            loadPlugin();
            isLoaded = true;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final void onEnable() {
        if (!isLoaded) return;
        try {
            enablePlugin();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final void onDisable() {
        if (!isLoaded) return;
        try {
            disablePlugin();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /* ---------------------------------------------------------------------
     * Foundation Lifecycle
     * --------------------------------------------------------------------- */

    /**
     * Initializes managers and configurations.
     * <p>
     * Core managers are created here. Additional managers (including the
     * global MessagesManager/PlaceholderManager owned by PaperFoundationAPI)
     * are provided by {@link #getAdditionalManagers()} and are discovered by
     * {@link CommonManagerLoader}.
     */
    protected void loadPlugin() {
        menuManager = new MenuManager<>(instance);
        itemManager = new ItemManager<>(instance);
        fileManager = new FileManager(this, getGson());
        startupManager = new StartupManager<>(this, getStartupConfiguration());
        internalMessageManager = new InternalMessageManager(this);
        dependencyManager = new DependencyManager(this);

        commonManagerLoader = new CommonManagerLoader(this);

        foundationLogger = FoundationLoggerService.getPluginLogger(this);
        foundationLogger.instantiateConfigurationHandler();
        foundationLogger.populateCommonManagers();

        commonManagerLoader.loadManagers();

        getPluginConfiguration().load();

        registerPaperLifecycle();

        try {
            onPluginLoad();
            LocalisationService.getInstance().registerPluginForLocalisation(this);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enables managers, startup tasks, and components.
     */
    protected void enablePlugin() throws Throwable {
        startTime = System.nanoTime();

        commonManagerLoader.enableManagers();

        startupManager.printStartupInfo();
        startupManager.getSortedTasks(startTime).forEach(StartupTask::run);

        onPluginEnable();

        startupManager.finishSetup(startTime);
    }

    /**
     * Shuts down all components and managers.
     */
    protected void disablePlugin() throws Throwable {
        onPluginDisable();
        disableComponents();
        commonManagerLoader.disableManagers();
        FoundationLoggerService.unregister(this);
    }

    /* ---------------------------------------------------------------------
     * Components
     * --------------------------------------------------------------------- */

    public void disableComponents() {
        getComponents().forEach(FoundationComponent::stop);
    }

    /* ---------------------------------------------------------------------
     * Configuration
     * --------------------------------------------------------------------- */

    protected AutoReloadableConfigurationHandler<StartupConfiguration> getStartupConfiguration() {
        if (startupConfigurationHandler == null) {
            startupConfigurationHandler = new AutoReloadableConfigurationHandler<>(
                    ConfigurationHandler.builder(this,
                            StartupConfiguration.class,
                            "configuration/startup.conf"),
                    true);
        }
        return startupConfigurationHandler;
    }

    /**
     * Messages configuration is NOT used by normal plugins – only by the
     * core PaperFoundationAPI plugin that owns the single MessagesManager.
     * It can still call this helper from its own code if desired.
     */
    protected AutoReloadableConfigurationHandler<MessagesConfiguration> getMessagesConfiguration() {
        if (messagesConfiguration == null) {
            messagesConfiguration = new AutoReloadableConfigurationHandler<>(
                    ConfigurationHandler.builder(this,
                            MessagesConfiguration.class,
                            "configuration/messages.conf"),
                    true);
        }
        return messagesConfiguration;
    }

    public @NotNull AutoReloadableConfigurationHandler<GLOBAL_CONFIGURATION> getPluginConfiguration() {
        if (configurationHandler == null) {
            configurationHandler = new AutoReloadableConfigurationHandler<>(
                    ConfigurationHandler.builder(this,
                            getGlobalConfigurationClass(),
                            "configuration/global.conf"),
                    true);
        }
        return configurationHandler;
    }

    /* ---------------------------------------------------------------------
     * FoundationPaperPlugin additional Manager Getters
     * --------------------------------------------------------------------- */

    public @NotNull ItemManager<T> getItemManager() {
        return itemManager;
    }

    public @NotNull MenuManager<T> getMenuManager() {
        return menuManager;
    }

    /* ---------------------------------------------------------------------
     * FoundationDefaultPlugin Implementation
     * --------------------------------------------------------------------- */

    @Override
    public @NotNull FileManager getFileManager() {
        return fileManager;
    }

    @Override
    public @NotNull StartupManager<StartupConfiguration> getStartupManager() {
        return startupManager;
    }

    @Override
    public @NotNull InternalMessageManager getInternalMessageManager() {
        return internalMessageManager;
    }

    /**
     * Default behaviour for NORMAL plugins:
     * - they do NOT own their own MessagesManager
     * - they use the global instance exposed by LocalisationService
     * <p>
     * PaperFoundationAPI overrides this to return its single, owned instance.
     */
    @Override
    public @NotNull MessagesManager<MessagesConfiguration> getMessagesManager() {
        return LocalisationService.getInstance().getMessagesManager();
    }

    /**
     * Default behaviour for NORMAL plugins:
     * - they do NOT own their own PlaceholderManager
     * - they use the global instance exposed by LocalisationService
     * <p>
     * PaperFoundationAPI overrides this to return its single, owned instance.
     */
    @Override
    public @NotNull PlaceholderManager getPlaceholderManager() {
        return LocalisationService.getInstance().getPlaceholderManager();
    }

    @Override
    public @NotNull DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    @Override
    public @Nullable InputStream getDefaultResource(String path) {
        return getResource(path);
    }

    @Override
    public @NotNull List<CommonManager> getCommonManagers() {
        return commonManagerLoader.getManagers();
    }

    @Override
    public @NotNull Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }

    /* ---------------------------------------------------------------------
     * Metadata
     * --------------------------------------------------------------------- */

    @Override
    public @NotNull String getPluginName() {
        return getPluginMeta().getName();
    }

    @Override
    public @NotNull String getPluginVersion() {
        return getPluginMeta().getVersion();
    }

    @Override
    public @NotNull String getPluginAuthor() {
        return String.join(", ", getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getMinecraftVersion() {
        return getServer().getVersion();
    }

    /* ---------------------------------------------------------------------
     * Utilities
     * --------------------------------------------------------------------- */

    @Override
    public @NotNull FoundationPluginLogger getFoundationPluginLogger() {
        return foundationLogger;
    }

    public FoundationPaperServer getFoundationPaperServer() {
        return foundationPaperServer;
    }

    @Override
    public @NotNull ComponentLogger getComponentLogger() {
        return ComponentLogger.logger(this.getLogger().getName());
    }

    /**
     * Schedules safe plugin disabling on the main thread.
     */
    @Override
    public void scheduleDisable() {
        Bukkit.getScheduler().runTask(
                this,
                () -> Bukkit.getPluginManager().disablePlugin(this)
        );
    }

    /* ---------------------------------------------------------------------
     * Paper Lifecycle helpers (unchanged)
     * --------------------------------------------------------------------- */

    private void registerPaperLifecycle() {
        var lifecycle = getLifecycleManager();
        lifecycle.registerEventHandler(
                LifecycleEvents.COMMANDS,
                event -> {
                }
        );
    }

    private void registerDataLifecycle() {
        var lifecycle = getLifecycleManager();
        lifecycle.registerEventHandler(
                LifecycleEvents.COMMANDS,
                event -> {
                }
        );
    }
}