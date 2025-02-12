package com.itsradiix.foundationapi.common.messages.providers;

import com.itsradiix.foundationapi.common.messages.languages.Language;

import java.util.Locale;
import java.util.Map;

public interface LanguageProvider {
    Map<Locale, Language> getLanguagesByLocale();
}
