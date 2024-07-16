package com.itsradiix.foundationapi.common.textmanager.languages.providers;
import com.itsradiix.foundationapi.common.datamanagement.files.converter.YAMLConverter;
import com.itsradiix.foundationapi.common.datamanagement.files.handlers.FolderHandler;
import com.itsradiix.foundationapi.common.textmanager.MessagesManager;
import com.itsradiix.foundationapi.common.textmanager.TextCreator;
import com.itsradiix.foundationapi.common.textmanager.languages.Language;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FilesLanguageProvider implements LanguageProvider {
    private final FolderHandler<Map<String, Object>> locales;

    public FilesLanguageProvider(String folder) {
        locales = new FolderHandler<>(folder, new YAMLConverter<>(),true,true);
    }

    @Override
    public Map<Locale, Language> getLanguagesByLocale() {

        Map<Locale, Language> languagesByLocale = new HashMap<>();
        List<Map<String, Object>> files = locales.getObjects();

        for (Map<String, Object> language : files) {
            try {
                String locale = (String) language.get("locale");
                if (locale.equalsIgnoreCase("TAGS") || locale.equalsIgnoreCase("TAG")) {
                    Map<String, String> tags = new HashMap<>();
                    language.forEach((key, value) -> {
                        if (value instanceof String string) {
                            tags.put(key, string);
                        }
                    });
                    TextCreator.addTagResolvers(tags);
                } else {
                    languagesByLocale.put(Locale.forLanguageTag(locale), new Language(language));
                }
            } catch (Exception e){
                MessagesManager.getInstance().getComponentLogger().warn("<red>Could not identify locale: {}", language.get("locale"));
            }
        }
        return languagesByLocale;

    }
}
