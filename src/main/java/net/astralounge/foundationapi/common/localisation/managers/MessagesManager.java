package net.astralounge.foundationapi.common.localisation.managers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.astralounge.foundationapi.common.datamanagement.database.DatabaseManager;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.AutoReloadableConfigurationHandler;
import net.astralounge.foundationapi.common.datamanagement.files.manager.FileManager;
import net.astralounge.foundationapi.common.localisation.FoundationMiniMessage;
import net.astralounge.foundationapi.common.localisation.LegacyMiniMessageConverter;
import net.astralounge.foundationapi.common.localisation.LocalisationService;
import net.astralounge.foundationapi.common.localisation.TextCreator;
import net.astralounge.foundationapi.common.localisation.configuration.MessagesConfiguration;
import net.astralounge.foundationapi.common.localisation.languages.Language;
import net.astralounge.foundationapi.common.localisation.languages.providers.DatabaseLanguageProvider;
import net.astralounge.foundationapi.common.localisation.languages.providers.FilesLanguageProvider;
import net.astralounge.foundationapi.common.localisation.languages.providers.LanguageProvider;
import net.astralounge.foundationapi.common.localisation.languages.providers.LanguageProviderType;
import net.astralounge.foundationapi.common.localisation.languages.providers.database.MessageStore;
import net.astralounge.foundationapi.common.localisation.languages.providers.database.OrmLiteMessageStore;
import net.astralounge.foundationapi.common.localisation.languages.providers.database.PerLocaleOrmLiteMessageStore;
import net.astralounge.foundationapi.common.localisation.placeholders.PlaceholderInformation;
import net.astralounge.foundationapi.common.logger.loggers.FoundationLogger;
import net.astralounge.foundationapi.common.manager.CommonManager;
import net.astralounge.foundationapi.common.manager.ConfigurableCommonManager;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.*;

@SuppressWarnings("unused")
public class MessagesManager<T extends MessagesConfiguration> extends ConfigurableCommonManager<T> {

    private final TextCreator textCreator;
    private final FoundationMiniMessage foundationMiniMessage;

    public record CacheKey(Locale locale, String key) { }

    /**
     * Cache: (locale, messageKey) -> MiniMessage string.
     */
    private Cache<CacheKey, String> miniCache;
    private LanguageProvider provider;

    /**
     * Files-based provider that aggregates all plugins' locale/ folders
     * and is used as a defaults source (even when DATABASE is active).
     */
    private FilesLanguageProvider filesDefaultsProvider;

    public MessagesManager(FoundationDefaultPlugin<?> plugin, AutoReloadableConfigurationHandler<T> configuration) {
        super(plugin, configuration);
        this.textCreator = new TextCreator();
        this.foundationMiniMessage = new FoundationMiniMessage();
    }

    @Override
    public void onLoad() {
        getConfigurationHandler().load();
    }

    @Override
    public void onEnable() throws Exception {
        this.provider = getProvider();
        this.miniCache = Caffeine.newBuilder()
                .maximumSize(getConfiguration().parsedMessagesCacheSize)
                .expireAfterAccess(Duration.ofMinutes(getConfiguration().parsedMessagesExpireMinutes))
                .build();
    }

    @Override
    public void onDisable() {
        this.provider = null;
        this.miniCache.invalidateAll();
    }

    @Override
    public Collection<Class<? extends CommonManager>> getCommonDependencies() {
        return List.of(FileManager.class, PlaceholderManager.class, DatabaseManager.class);
    }

    public Component getMessage(Locale locale, String messageKey, TagResolver... tagResolvers) {
        return getMessage(locale, messageKey, null, tagResolvers);
    }

    public Component getMessage(Locale locale, String messageKey, UUID playerOne, TagResolver... tagResolvers) {
        return getMessage(locale, messageKey, playerOne, null, tagResolvers);
    }

    public Component getMessage(Locale locale, String messageKey, UUID playerOne, UUID playerTwo, TagResolver... tagResolvers) {
        return textCreator.createUsingCachedLegacy(
                new PlaceholderInformation(playerOne, playerTwo, getMiniMessageText(locale, messageKey)),
                tagResolvers);
    }

    /**
     * Return a MiniMessage string for the given locale+key, using provider for raw access and
     * caching the legacy->Mini conversion.
     */
    private String getMiniMessageText(Locale locale, String messageKey) {
        CacheKey cacheKey = new CacheKey(locale, messageKey);

        String cached = miniCache.getIfPresent(cacheKey);
        if (cached != null) {
            return cached;
        }

        try {
            String raw = provider.getRaw(locale, messageKey);
            if (raw == null) {
                raw = provider.getRaw(getDefaultLocale(), messageKey);
            }
            if (raw == null) {
                throw new IllegalArgumentException("Missing message: " + messageKey);
            }

            String mini = LegacyMiniMessageConverter.legacyToMiniFast(raw);
            miniCache.put(cacheKey, mini);
            return mini;
        } catch (Exception e) {
            getLogger().warn(e.getMessage());
            getLogger().warn("Cannot get message with key: {}", messageKey);
            return "{" + messageKey + "}";
        }
    }

    /**
     * Expose Language objects built from the provider cache, for listing or editor views.
     */
    public Map<Locale, Language> getLanguagesByLocale() {
        return provider.getLanguagesByLocale();
    }

    /**
     * Reload from the current provider and clear MiniMessage cache.
     */
    public void reload() {
        try {
            provider.reload();
        } catch (Exception e) {
            getLogger().error("Failed to reload LanguageProvider", e);
        }
        miniCache.invalidateAll();

    }

    /**
     * Change the active language provider type and reload messages.
     * Does NOT copy messages between backends; for export/import, use exportMessages().
     */
    public void setProvider(LanguageProviderType providerType) {
        getConfiguration().defaultLanguageProvider = providerType;
        this.provider = createProvider(providerType);
        reload();
    }

    /**
     * Update a single message (raw text) for locale+key and update the MiniMessage cache.
     * Intended for in-game editors.
     */
    public void setMessage(Locale locale, String messageKey, String rawMessage) throws Exception {
        provider.setRaw(locale, messageKey, rawMessage);
        CacheKey key = new CacheKey(locale, messageKey);
        miniCache.put(key, LegacyMiniMessageConverter.legacyToMiniFast(rawMessage));
    }

    /**
     * Export all messages from the current provider into a target provider type.
     * <p>
     * Example use cases:
     * - FILES -> DATABASE (migrate to DB-backed messages)
     * - DATABASE -> FILES (dump to disk for manual editing or backup)
     *
     * @param targetType        FILES or DATABASE
     * @param switchAfterExport if true, switches this MessagesManager to use the target provider
     */
    public void exportMessages(LanguageProviderType targetType, boolean switchAfterExport) throws Exception {
        LanguageProvider source = (provider != null) ? provider : getProvider();
        // snapshot current messages
        Map<Locale, Language> sourceLanguages = source.getLanguagesByLocale();

        LanguageProvider target = createProvider(targetType);
        // ensure target has its own cache up
        target.reload();

        for (Map.Entry<Locale, Language> localeEntry : sourceLanguages.entrySet()) {
            Locale locale = localeEntry.getKey();
            Map<String, String> messages = localeEntry.getValue().messages();
            for (Map.Entry<String, String> msgEntry : messages.entrySet()) {
                String key = msgEntry.getKey();
                String raw = msgEntry.getValue();
                if (raw != null) {
                    target.setRaw(locale, key, raw);
                }
            }
        }

        if (switchAfterExport) {
            getConfiguration().defaultLanguageProvider = targetType;
            this.provider = target;
            reload();
            getLogger().info("Switched MessagesManager to {} provider after export", targetType);
        } else {
            getLogger().info("Export to {} provider completed (no provider switch)", targetType);
        }
    }

    public FoundationLogger getLogger() {
        return getPlugin().getFoundationPluginLogger();
    }

    public TextCreator getTextCreator() {
        return textCreator;
    }

    public FoundationMiniMessage getFoundationMiniMessage() {
        return foundationMiniMessage;
    }

    public MiniMessage getMiniMessage() {
        // Delegate to the global FoundationMiniMessage
        return LocalisationService.getInstance()
                .getFoundationMiniMessage()
                .getMiniMessage();
    }

    public Locale getDefaultLocale() {
        return Locale.of(getConfiguration().defaultLocale);
    }

    /**
     * Lazily initialises the main provider based on configuration.
     */
    private LanguageProvider getProvider() {
        if (provider == null) {
            LanguageProviderType providerType = getConfiguration().defaultLanguageProvider;
            provider = createProvider(providerType);
            try {
                provider.reload();
            } catch (Exception e) {
                getLogger().error("Failed to reload LanguageProvider of type {}", providerType, e);
            }
        }

        return provider;
    }

    /**
     * Create a fresh provider instance for a given type, without mutating current state.
     * Also ensures filesDefaultsProvider is available when DATABASE is in use.
     */
    private LanguageProvider createProvider(LanguageProviderType providerType) {
        MessagesConfiguration cfg = getConfiguration();

        switch (providerType) {
            case DATABASE -> {
                try {
                    MessageStore store = getMessageStore(getPlugin().getCommonManager(DatabaseManager.class));
                    DatabaseLanguageProvider dbProvider = new DatabaseLanguageProvider(getPlugin(), store, getConfigurationHandler());
                    return dbProvider;
                } catch (Exception e) {
                    getLogger().error("Failed to initialize DatabaseLanguageProvider, falling back to FilesLanguageProvider", e);
                    // Fall back to FILES as active provider
                    return createFilesProvider(cfg);
                }
            }
            default -> {
                // FILES is both active provider and defaults provider
                filesDefaultsProvider = new FilesLanguageProvider(
                        getPlugin(),
                        getConfigurationHandler()
                );
                return filesDefaultsProvider;
            }
        }
    }

    private FilesLanguageProvider createFilesProvider(MessagesConfiguration cfg) {
        FilesLanguageProvider filesProvider = new FilesLanguageProvider(
                getPlugin(),
                getConfigurationHandler()
        );
        try {
            filesProvider.reload();
        } catch (Exception e) {
            getLogger().error("Failed to load file-based languages", e);
        }
        return filesProvider;
    }

    private @NonNull MessageStore getMessageStore(DatabaseManager databaseManager) throws Exception {
        if (databaseManager == null) {
            throw new IllegalStateException("DatabaseManager is not loaded!");
        }

        MessagesConfiguration cfg = getConfiguration();
        return switch (cfg.databaseLayout) {
            case PER_LOCALE -> new PerLocaleOrmLiteMessageStore(databaseManager);
            case SINGLE_TABLE -> new OrmLiteMessageStore(databaseManager);
        };
    }

    private MessagesConfiguration getConfiguration() {
        return getConfigurationHandler().getConfigurationObject();
    }

    /**
     * Initialise the FilesLanguageProvider with all registered plugins, and
     * if DATABASE is active, seed missing defaults from files into DB.
     * <p>
     * This is intended to be called once from LocalisationService.onEnable(),
     * after all plugins have had a chance to register for localisation.
     */
    public void initialiseFilesProviderWithPlugins(@NotNull Collection<FoundationDefaultPlugin<?>> plugins) {
        getLogger().debug("Initialising FilesLanguageProvider with plugins: {}", plugins);
        if (plugins.isEmpty()) {
            return;
        }

        MessagesConfiguration cfg = getConfiguration();

        // Ensure filesDefaultsProvider exists
        if (filesDefaultsProvider == null) {
            filesDefaultsProvider = new FilesLanguageProvider(
                    getPlugin(),
                    getConfigurationHandler()
            );
        }

        // Register all plugins' locale folders
        for (FoundationDefaultPlugin<?> plugin : plugins) {
            getLogger().debug("Registering plugin {} for FilesLanguageProvider", plugin.getPluginName());
            filesDefaultsProvider.registerPlugin(plugin);
        }

        // Reload files provider once to load placeholders from all plugins
        try {
            filesDefaultsProvider.reload();
            filesDefaultsProvider.reloadPlaceholders();
        } catch (Exception e) {
            getLogger().error("Failed to reload FilesLanguageProvider during initialisation", e);
        }

        // If DATABASE is active, seed missing defaults from files into DB
        if (provider instanceof DatabaseLanguageProvider dbProvider) {
            getLogger().debug("Seeding missing DB defaults from files");
            Map<Locale, Language> fileLangs = filesDefaultsProvider.getLanguagesByLocale();

            for (Map.Entry<Locale, Language> localeEntry : fileLangs.entrySet()) {
                Locale locale = localeEntry.getKey();
                Map<String, String> messages = localeEntry.getValue().messages();

                for (Map.Entry<String, String> msgEntry : messages.entrySet()) {
                    String key = msgEntry.getKey();
                    String fileValue = msgEntry.getValue();
                    if (fileValue == null) continue;

                    String dbValue = dbProvider.getRaw(locale, key);
                    if (dbValue == null) {
                        try {
                            dbProvider.setRaw(locale, key, fileValue);
                            // Update MiniMessage cache for this entry
                            setMessage(locale, key, fileValue);
                        } catch (Exception e) {
                            getLogger().error(
                                    "Failed to seed DB default for key {} locale {}",
                                    key, locale, e
                            );
                        }
                    }
                }
            }
        }

        // Clear MiniMessage cache once so new messages/placeholders are visible
        foundationMiniMessage.reloadMiniMessage();
        miniCache.cleanUp();
    }
}