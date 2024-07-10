package nl.bryansuk.foundationapi.common.textmanager.languages.providers;
import nl.bryansuk.foundationapi.common.datamanagement.files.converter.YAMLConverter;
import nl.bryansuk.foundationapi.common.datamanagement.files.handlers.FolderHandler;
import nl.bryansuk.foundationapi.common.textmanager.MessagesManager;
import nl.bryansuk.foundationapi.common.textmanager.TextCreator;
import nl.bryansuk.foundationapi.common.textmanager.languages.Language;

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
                    languagesByLocale.put(Locale.of(locale), new Language(language));
                }
            } catch (Exception e){
                MessagesManager.getInstance().getComponentLogger().warn("<red>Could not identify locale: {0}", language.get("locale"));
            }
        }
        return languagesByLocale;

    }
}
