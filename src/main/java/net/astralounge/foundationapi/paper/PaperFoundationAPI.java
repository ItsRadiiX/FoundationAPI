package net.astralounge.foundationapi.paper;

import net.astralounge.foundationapi.common.component.FoundationComponent;
import net.astralounge.foundationapi.common.datamanagement.database.DatabaseManager;
import net.astralounge.foundationapi.common.datamanagement.files.FileManagerService;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.AutoReloadableConfigurationHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.ConfigurationHandler;
import net.astralounge.foundationapi.common.dependencies.PluginDependency;
import net.astralounge.foundationapi.common.localisation.LocalisationService;
import net.astralounge.foundationapi.common.localisation.configuration.MessagesConfiguration;
import net.astralounge.foundationapi.common.localisation.managers.MessagesManager;
import net.astralounge.foundationapi.common.localisation.managers.PlaceholderManager;
import net.astralounge.foundationapi.common.logger.service.FoundationLoggerService;
import net.astralounge.foundationapi.common.manager.CommonManager;
import net.astralounge.foundationapi.common.playerdata.managers.PlayerDataManager;
import net.astralounge.foundationapi.common.playerdata.services.PlayerDataService;
import net.astralounge.foundationapi.common.plugin.configuration.GlobalConfiguration;
import net.astralounge.foundationapi.common.startup.StartupTask;
import net.astralounge.foundationapi.paper.dependencies.PAPISoftDependency;
import net.astralounge.foundationapi.paper.playerdata.PaperPlayerDataManager;
import net.astralounge.foundationapi.paper.plugin.FoundationPaperPlugin;
import net.astralounge.foundationapi.paper.translator.PlaceholderAPIHook;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PaperFoundationAPI extends FoundationPaperPlugin<PaperFoundationAPI, GlobalConfiguration> {

    private final FileManagerService fileManagerService = FileManagerService.getInstance();
    private final PlayerDataService playerDataService = PlayerDataService.getInstance();

    // The single, global localisation managers owned by this plugin
    private DatabaseManager databaseManager;
    private MessagesManager<MessagesConfiguration> messagesManager;
    private PlaceholderManager placeholderManager;
    private PlayerDataManager playerDataManager;

    @Override
    public @NotNull List<PluginDependency> getPluginDependencies() {
        return List.of(new PAPISoftDependency<>(PlaceholderAPIHook.class));
    }

    @Override
    public @NotNull List<CommonManager> getAdditionalManagers() {
        List<CommonManager> additional = new ArrayList<>();

        // Lazily create global managers
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(this);
        }

        // Lazily create the single global localisation managers
        if (messagesManager == null || placeholderManager == null) {
            AutoReloadableConfigurationHandler<MessagesConfiguration> messagesConfig =
                    new AutoReloadableConfigurationHandler<>(
                            ConfigurationHandler.builder(this,
                                    MessagesConfiguration.class,
                                    "configuration/messages.conf"),
                            true
                    );
            messagesManager = new MessagesManager<>(this, messagesConfig);
            placeholderManager = new PlaceholderManager(this);
        }

        // Lazily create global managers
        if (playerDataManager == null) {
            playerDataManager = new PaperPlayerDataManager(this);
        }

        additional.add(databaseManager);
        additional.add(messagesManager);
        additional.add(placeholderManager);
        additional.add(playerDataManager);
        return additional;
    }

    @Override
    public @NotNull List<FoundationComponent<PaperFoundationAPI>> getComponents() {
        return List.of();
    }

    @Override
    public @NotNull List<StartupTask> startupTasks() {
        return List.of();
    }

    @Override
    protected Class<GlobalConfiguration> getGlobalConfigurationClass() {
        return GlobalConfiguration.class;
    }

    @Override
    public void onLoad() {
        FoundationLoggerService.initGlobalLogger(this);
        assert fileManagerService.registerPlugin(this) != null;
        FoundationLoggerService.initGlobalConfigHandler();
        super.onLoad();
    }

    @Override
    public void onPluginLoad() throws Throwable {
        // Now that our localisation managers exist (via getAdditionalManagers()),
        // we can initialise the global LocalisationService façade.
        LocalisationService.init(this);
    }

    @Override
    public void onPluginEnable() throws Throwable {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            LocalisationService.getInstance().initialiseAfterManagersEnabled();
        });
    }

    @Override
    public void onPluginDisable() throws Throwable {
        // Underlying managers are disabled by CommonManagerLoader in the base class.
    }

    // -------------------------------------------------------------------------
    // Provide direct access to the single global localisation managers
    // -------------------------------------------------------------------------

    @Override
    public @NotNull MessagesManager<MessagesConfiguration> getMessagesManager() {
        if (messagesManager == null) {
            throw new IllegalStateException("MessagesManager has not been initialised yet");
        }
        return messagesManager;
    }

    @Override
    public @NotNull PlaceholderManager getPlaceholderManager() {
        if (placeholderManager == null) {
            throw new IllegalStateException("PlaceholderManager has not been initialised yet");
        }
        return placeholderManager;
    }
}