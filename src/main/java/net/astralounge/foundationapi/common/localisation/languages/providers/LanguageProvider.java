package net.astralounge.foundationapi.common.localisation.languages.providers;

import net.astralounge.foundationapi.common.localisation.languages.Language;

import java.util.Locale;
import java.util.Map;

public interface LanguageProvider {

    /** Rebuilds internal caches from underlying storage. */
    void reload() throws Exception;

    void reloadPlaceholders();

    /** Get the raw message for a locale + key, or null if missing. */
    String getRaw(Locale locale, String key);

    /** Set/update a single message; implementation persists + updates cache. */
    void setRaw(Locale locale, String key, String message) throws Exception;

    /** Optional: bulk view for things like export, editor listings, etc. */
    Map<Locale, Language> getLanguagesByLocale();
}
