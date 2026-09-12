package net.astralounge.foundationapi.common.localisation.languages.providers.database;

import java.util.Locale;
import java.util.Map;

/**
 * Abstraction over message persistence.
 * Intended to be backed by SQL (e.g. ORMLite, Hibernate).
 * <p>
 * All keys are canonical IDs like "pluginId:key".
 */
public interface MessageStore {

    /**
     * Load all messages, grouped by locale.
     * Map structure:
     *   locale -> (messageKey -> messageText)
     */
    Map<Locale, Map<String, String>> loadAll() throws Exception;

    /**
     * Get a single message by locale and key, or null if missing.
     */
    String get(Locale locale, String key) throws Exception;

    /**
     * Upsert a single message for the given locale & key.
     */
    void set(Locale locale, String key, String message) throws Exception;

    /**
     * Optional bulk save for more efficient flushing.
     * Default is no-op.
     */
    default void saveAll(Map<Locale, Map<String, String>> messagesByLocale) throws Exception {
        // no-op by default
    }
}