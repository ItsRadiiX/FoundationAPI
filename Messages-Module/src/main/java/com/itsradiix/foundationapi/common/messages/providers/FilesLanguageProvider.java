package com.itsradiix.foundationapi.common.messages.providers;
import com.itsradiix.foundationapi.common.datamanagement.files.converter.YAMLConverter;
import com.itsradiix.foundationapi.common.datamanagement.files.handlers.FileHandler;
import com.itsradiix.foundationapi.common.datamanagement.files.handlers.FolderHandler;
import com.itsradiix.foundationapi.common.messages.MessagesManager;
import com.itsradiix.foundationapi.common.messages.languages.Language;
import com.itsradiix.foundationapi.common.textmanager.TextCreator;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FilesLanguageProvider implements LanguageProvider {
    private final FolderHandler<Map<String, String>> locales;

    public FilesLanguageProvider(String folder) {
        locales = new FolderHandler<>(folder, new YAMLConverter<>(),true,true);
    }

    @Override
    public Map<Locale, Language> getLanguagesByLocale() {
        Map<Locale, Language> languagesByLocale = new HashMap<>();
        List<FileHandler<Map<String, String>>> fileHandlers = locales.getFileHandlersList();

        for (FileHandler<Map<String, String>> fileHandler : fileHandlers) {

            String fileName = fileHandler.getFileNameWithoutExtension();

            MessagesManager.getInstance().getComponentLogger().info("{} is being loaded", fileName);

            Map<String, String> language = fileHandler.getObject();

            MessagesManager.getInstance().getComponentLogger().info("{} has the following contents: {}", fileName, language);

            try {
                if (fileName.equalsIgnoreCase("TAGS") || fileName.equalsIgnoreCase("TAG")) {
                    Map<String, String> tags = new HashMap<>();
                    if (language == null) return null;
                    language.forEach((key, value) -> {
                        if (value instanceof String string) {
                            MessagesManager.getInstance().getComponentLogger().info("adding {} tag", key);
                            tags.put(key, string);
                        }
                    });
                    TextCreator.addTagResolvers(tags);
                } else {
                    Locale locale = Locale.of(fileName);

                    MessagesManager.getInstance().getComponentLogger().info("Found language {}", locale);

                    languagesByLocale.put(Locale.of(fileName), new Language(language));
                }
            } catch (Exception e){
                MessagesManager.getInstance().getComponentLogger().info("<red>Could not identify locale: {}", fileName);
            }
        }
        return languagesByLocale;

    }
}
