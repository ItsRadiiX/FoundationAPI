package com.itsradiix.foundationapi.common.messages.languages;

import java.util.Locale;
import java.util.Map;

public interface LanguageProvider {
    Map<Locale, Language> getLanguagesByLocale();
}
