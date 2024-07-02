package nl.bryansuk.foundationapi.common.textmanager.languages.providers;

import nl.bryansuk.foundationapi.common.textmanager.languages.Language;

import java.util.Locale;
import java.util.Map;

public interface LanguageProvider {
    Map<Locale, Language> getLanguagesByLocale();
}
