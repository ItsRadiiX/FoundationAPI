package net.astralounge.foundationapi.common.localisation.languages.providers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.AutoReloadableConfigurationHandler;
import net.astralounge.foundationapi.common.localisation.configuration.MessagesConfiguration;
import net.astralounge.foundationapi.common.localisation.languages.Language;
import net.astralounge.foundationapi.common.localisation.languages.providers.database.MessageStore;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LanguageProvider backed by a MessageStore (typically DB).
 * <p>
 * Uses Caffeine to cache per-locale maps of messages with size and time-based eviction.
 */
public class DatabaseLanguageProvider implements LanguageProvider {

    private final FoundationDefaultPlugin<?> plugin;
    private final MessageStore store;

    /**
     * Cache of per-locale message maps.
     * Key   = locale
     * Value = (messageKey -> rawMessage)
     */
    private final Cache<Locale, Map<String, String>> localeCache;

    public DatabaseLanguageProvider(
            @NotNull FoundationDefaultPlugin<?> plugin,
            @NotNull MessageStore store,
            @NotNull AutoReloadableConfigurationHandler<? extends MessagesConfiguration> configurationHandler) {
        this.plugin = plugin;
        this.store = store;
        localeCache = Caffeine.newBuilder()
                .maximumSize(configurationHandler.getConfigurationObject().databaseCacheSize)
                .expireAfterAccess(Duration.ofMinutes(
                        configurationHandler.getConfigurationObject().databaseCacheExpireMinutes))
                .build();
    }

    @Override
    public void reload() {
        // Clear in-memory cache; underlying store remains untouched
        localeCache.invalidateAll();
    }

    @Override
    public void reloadPlaceholders() {
        // NO-OP as of now
    }

    @Override
    public String getRaw(Locale locale, String key) {
        try {
            Map<String, String> byKey = localeCache.get(locale, this::loadLocaleFromStore);
            if (byKey == null) {
                return null;
            }

            String value = byKey.get(key);
            if (value != null) {
                return value;
            }

            // Optional: lazy single-key load for new keys
            String fromDb = store.get(locale, key);
            if (fromDb != null) {
                byKey.put(key, fromDb);
            }
            return fromDb;
        } catch (Exception e) {
            plugin.getFoundationPluginLogger().error("Failed to get message {} for locale {}", key, locale, e);
            return null;
        }
    }

    @Override
    public void setRaw(Locale locale, String key, String message) throws Exception {
        store.set(locale, key, message);
        Map<String, String> byKey = localeCache.get(locale, l -> new ConcurrentHashMap<>());
        byKey.put(key, message);
    }

    @Override
    public Map<Locale, Language> getLanguagesByLocale() {
        Map<Locale, Language> result = new HashMap<>();
        try {
            Map<Locale, Map<String, String>> all = store.loadAll();

            // refresh cache with full view (used for admin/export operations)
            localeCache.invalidateAll();
            all.forEach((loc, map) -> {
                Map<String, String> copy = new ConcurrentHashMap<>(map);
                localeCache.put(loc, copy);
                result.put(loc, new Language(copy));
            });
        } catch (Exception e) {
            plugin.getFoundationPluginLogger().error("Failed to load languages from MessageStore", e);
        }

        return result;
    }

    /* ---------------------------------------------------------------------
     * Internal helpers
     * --------------------------------------------------------------------- */

    private Map<String, String> loadLocaleFromStore(Locale locale) {
        try {
            // loadAll() isn't ideal for one locale, but we can approximate:
            // if your store can be extended with loadLocale(locale), use that instead.
            Map<Locale, Map<String, String>> all = store.loadAll();
            Map<String, String> map = all.get(locale);
            if (map == null) {
                return new ConcurrentHashMap<>();
            }
            return new ConcurrentHashMap<>(map);
        } catch (Exception e) {
            plugin.getFoundationPluginLogger().error("Failed to load locale {} from MessageStore", locale, e);
            return new ConcurrentHashMap<>();
        }
    }
}