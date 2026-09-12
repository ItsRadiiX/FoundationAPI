package net.astralounge.foundationapi.common.localisation;

import net.astralounge.foundationapi.common.localisation.configuration.MessagesConfiguration;
import net.astralounge.foundationapi.common.localisation.managers.MessagesManager;
import net.astralounge.foundationapi.common.localisation.managers.PlaceholderManager;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Centralised localisation service:
 *  - One MessagesManager (from the Foundation API plugin)
 *  - One PlaceholderManager (from the Foundation API plugin)
 *  - One FoundationMiniMessage (from MessagesManager)
 *  - One TextCreator (from MessagesManager)
 *
 * Initialised once by FoundationAPI, then used by all plugins.
 *
 * NOTE:
 *  This is NOT a CommonManager. It is a global façade over the FoundationAPI plugin's
 *  common managers, which are created and lifecycle-managed by that plugin.
 */
public final class LocalisationService {

    private static volatile LocalisationService INSTANCE;

    private final MessagesManager<MessagesConfiguration> messagesManager;
    private final PlaceholderManager placeholderManager;

    /**
     * Plugins that want their locale/ files & placeholders to be part of the global system.
     * We only process this set once, when {@link #initialiseAfterManagersEnabled()} is called.
     */
    private final Set<FoundationDefaultPlugin<?>> registeredPlugins = new LinkedHashSet<>();

    /**
     * Guard so we only run the heavy initialisation once.
     */
    private boolean initialised;

    private LocalisationService(@NotNull FoundationDefaultPlugin<?> foundationApiPlugin) {

        // Use the plugin's own common managers; they are created and enabled by CommonManagerLoader.
        // At this time they may not be enabled yet, but that's fine – we only *use* them in
        // initialiseAfterManagersEnabled(), which is called after managers are enabled.
        this.messagesManager = foundationApiPlugin.getMessagesManager();
        this.placeholderManager = foundationApiPlugin.getPlaceholderManager();
    }

    /**
     * Initialise the global LocalisationService façade.
     * <p>
     * Should be called once by the FoundationAPI plugin (e.g. in its onLoad, after its
     * managers have been constructed).
     */
    public static void init(@NotNull FoundationDefaultPlugin<?> foundationApiPlugin) {
        synchronized (LocalisationService.class) {
            if (INSTANCE != null) {
                throw new IllegalStateException("LocalisationService is already initialised");
            }
            INSTANCE = new LocalisationService(foundationApiPlugin);
        }
    }

    public static LocalisationService getInstance() {
        LocalisationService instance = INSTANCE;
        if (instance == null) {
            throw new IllegalStateException("LocalisationService has not been initialised yet");
        }
        return instance;
    }

    /**
     * Register a plugin for localisation:
     *  - Its locale files will be considered by the global FILES provider.
     *  - Its placeholders from locale/placeholders.* will be registered into the global PlaceholderManager.
     *  - In DATABASE mode, its defaults will be seeded into DB for missing keys.
     *
     * This method is cheap; it only records the plugin in a set.
     * Actual scanning/seeding happens once in {@link #initialiseAfterManagersEnabled()}.
     *
     * Usually called from each plugin's onLoad (recommended) or onEnable.
     */
    public void registerPluginForLocalisation(FoundationDefaultPlugin<?> plugin) {
        registeredPlugins.add(plugin);
    }

    /**
     * Called once, after the FoundationAPI plugin has:
     *  - Loaded and enabled all its CommonManagers (FileManager, DatabaseManager, PlaceholderManager, MessagesManager, ...)
     *  - Given other plugins a chance to call {@link #registerPluginForLocalisation(FoundationDefaultPlugin)}.
     *
     * This will:
     *  - Initialise the FilesLanguageProvider with all registered plugins
     *  - In DATABASE mode, seed missing defaults from files into DB
     *  - Clear the MiniMessage cache so new messages/placeholders are visible
     */
    public synchronized void initialiseAfterManagersEnabled() {
        if (initialised) {
            return;
        }

        // Delegate the heavy lifting to MessagesManager
        messagesManager.initialiseFilesProviderWithPlugins(registeredPlugins);

        initialised = true;
    }

    public MessagesManager<MessagesConfiguration> getMessagesManager() {
        return messagesManager;
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public FoundationMiniMessage getFoundationMiniMessage() {
        return messagesManager.getFoundationMiniMessage();
    }

    public TextCreator getTextCreator() {
        return messagesManager.getTextCreator();
    }

    /**
     * Expose the set of registered plugins (read-only copy) if needed for diagnostics.
     */
    public Collection<FoundationDefaultPlugin<?>> getRegisteredPlugins() {
        return Set.copyOf(registeredPlugins);
    }
}