package net.astralounge.foundationapi.common.localisation.languages.providers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.AutoReloadableConfigurationHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.FolderHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.RegisteredFileHandler;
import net.astralounge.foundationapi.common.localisation.FoundationMiniMessage;
import net.astralounge.foundationapi.common.localisation.LocalisationService;
import net.astralounge.foundationapi.common.localisation.configuration.MessagesConfiguration;
import net.astralounge.foundationapi.common.localisation.languages.Language;
import net.astralounge.foundationapi.common.localisation.managers.PlaceholderManager;
import net.astralounge.foundationapi.common.localisation.placeholders.FoundationPlaceholder;
import net.astralounge.foundationapi.common.localisation.placeholders.PlaceholderId;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import io.leangen.geantyref.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FILES-based LanguageProvider that can aggregate locale files from multiple plugins.
 * <p>
 * Each registered plugin is expected to have:
 *   dataFolder/locale/<localeTag>.(json|conf|yml|...)
 *   dataFolder/locale/placeholders.(json|conf|yml|...)
 * <p>
 * This provider:
 *   - Merges all plugin locales into a single logical view per Locale.
 *   - Registers placeholders from all plugins into the global PlaceholderManager.
 *   - Uses Caffeine to cache per-locale message maps with eviction.
 */
public class FilesLanguageProvider implements LanguageProvider {

    /**
     * The "owner" plugin, used only for logging / configuration.
     * For the central MessagesManager, this will be the FoundationAPI plugin.
     */
    private final FoundationDefaultPlugin<?> ownerPlugin;

    /**
     * Per-plugin FolderHandlers for locale files (rooted at each plugin's dataFolder/locale).
     */
    private final Map<FoundationDefaultPlugin<?>, FolderHandler<Map<String, String>>> pluginFolders =
            new ConcurrentHashMap<>();

    /**
     * In-memory cache: locale -> (messageKey -> rawMessage).
     * Loaded lazily per locale from all registered plugins' folders.
     */
    private final Cache<Locale, Map<String, String>> localeCache;

    public FilesLanguageProvider(FoundationDefaultPlugin<?> ownerPlugin,
                                 AutoReloadableConfigurationHandler<? extends MessagesConfiguration> configurationHandler) {
        this.ownerPlugin = ownerPlugin;
        MessagesConfiguration cfg = configurationHandler.getConfigurationObject();
        this.localeCache = Caffeine.newBuilder()
                .maximumSize(cfg.databaseCacheSize)
                .expireAfterAccess(Duration.ofMinutes(cfg.databaseCacheExpireMinutes))
                .build();
    }

    /**
     * Register a plugin whose locale files should be included in this provider.
     * This should be called from LocalisationService when a plugin is registered
     * for localisation.
     */
    public void registerPlugin(FoundationDefaultPlugin<?> plugin) {
        pluginFolders.computeIfAbsent(plugin, p ->
                new FolderHandler<>(
                        p,
                        new TypeToken<Map<String, String>>() {}.getType(),
                        "locale",
                        true,
                        true
                )
        );
    }

    @Override
    public void reload() {
        // Clear in-memory maps and re-register placeholders from all plugins' files
        localeCache.invalidateAll();
    }

    @Override
    public void reloadPlaceholders() {
        PlaceholderManager globalPlaceholderManager = LocalisationService
                .getInstance()
                .getPlaceholderManager();
        FoundationMiniMessage globalMiniMessage = LocalisationService
                .getInstance()
                .getFoundationMiniMessage();

        for (Map.Entry<FoundationDefaultPlugin<?>, FolderHandler<Map<String, String>>> entry : pluginFolders.entrySet()) {

            FoundationDefaultPlugin<?> plugin = entry.getKey();
            FolderHandler<Map<String, String>> folder = entry.getValue();

            plugin.getFoundationPluginLogger().debug("Registering placeholders from {}", folder.getPath());

            for (RegisteredFileHandler<Map<String, String>> fileHandler : folder.getFileHandlersList()) {
                String fileName = fileHandler.getFile().getName().replaceFirst("\\.[^.]+$", "");
                plugin.getFoundationPluginLogger().debug("File name: {} is being checked", fileName);
                if (!fileName.equalsIgnoreCase("placeholders")) continue;

                plugin.getFoundationPluginLogger().debug("{} is being loaded (placeholders)", fileHandler.getPath());
                List<FoundationPlaceholder> placeholders = getPlaceholders(plugin, fileHandler);
                globalPlaceholderManager.addPlaceholders(placeholders);
            }
        }

        // After registering all placeholders, rebuild MiniMessage so tags are available
        globalMiniMessage.reloadMiniMessage();
    }

    @Override
    public String getRaw(Locale locale, String key) {
        Map<String, String> byKey = localeCache.get(locale, this::loadLocaleFromAllPlugins);
        if (byKey == null) {
            return null;
        }
        return byKey.get(key);
    }

    @Override
    public void setRaw(Locale locale, String key, String message) throws Exception {
        Map<String, String> byKey = localeCache.get(locale, l -> new HashMap<>());
        byKey.put(key, message);

        // TODO: if you want GUI edits to be persisted back into individual plugin files,
        // you'll need a policy for which plugin "owns" which key and write via its FolderHandler.
        ownerPlugin.getFoundationPluginLogger().debug(
                "Updated file-backed message [{}] for locale {} in memory (persistence to disk not yet implemented)",
                key, locale
        );
    }

    @Override
    public Map<Locale, Language> getLanguagesByLocale() {
        // Snapshot view of currently cached locales only
        Map<Locale, Language> result = new HashMap<>();
        localeCache.asMap().forEach((loc, map) -> result.put(loc, new Language(new HashMap<>(map))));
        return result;
    }

    /* ---------------------------------------------------------------------
     * Internal helpers
     * --------------------------------------------------------------------- */

    private @NotNull List<FoundationPlaceholder> getPlaceholders(
            FoundationDefaultPlugin<?> plugin,
            RegisteredFileHandler<Map<String, String>> fileHandler) {
        Map<String, String> filePlaceholders = fileHandler.getObject();
        if (filePlaceholders == null) return Collections.emptyList();

        List<FoundationPlaceholder> foundationPlaceholders = new ArrayList<>();
        filePlaceholders.forEach((key, value) -> {
            if (value != null) {
                String id = PlaceholderId.namespaced(plugin, key);
                plugin.getFoundationPluginLogger().debug("Adding Placeholder {} (local id: {})", id, key);
                foundationPlaceholders.add(new FoundationPlaceholder() {
                    @Override
                    public String getTagIdentifier() {
                        return id;
                    }

                    @Override
                    public String resolveStatic() {
                        return value;
                    }
                });
            }
        });
        return foundationPlaceholders;
    }

    /**
     * Load a single locale by aggregating all plugins' locale/<localeTag>.* files.
     */
    private Map<String, String> loadLocaleFromAllPlugins(Locale locale) {
        Map<String, String> aggregated = new HashMap<>();

        for (Map.Entry<FoundationDefaultPlugin<?>, FolderHandler<Map<String, String>>> entry : pluginFolders.entrySet()) {
            FoundationDefaultPlugin<?> plugin = entry.getKey();
            FolderHandler<Map<String, String>> folder = entry.getValue();

            for (RegisteredFileHandler<Map<String, String>> fileHandler : folder.getFileHandlersList()) {
                String fileName = fileHandler.getFile().getName().replaceFirst("\\.[^.]+$", "");
                if (fileName.equalsIgnoreCase("placeholders")) continue;

                try {
                    Locale fileLocale = Locale.of(fileName);
                    if (!fileLocale.equals(locale)) continue;

                    plugin.getFoundationPluginLogger().debug("{} is being loaded for locale {}", fileHandler.getPath(), locale);
                    Map<String, String> language = fileHandler.getObject();
                    plugin.getFoundationPluginLogger().debug("{} has the following contents: {}", fileHandler.getPath(), language);

                    // Merge into aggregated map; later plugins can override keys if needed
                    aggregated.putAll(language);
                } catch (Exception e) {
                    plugin.getFoundationPluginLogger().warn("<red>Could not identify locale: {}", fileName);
                }
            }
        }

        return aggregated;
    }
}