package com.itsradiix.foundationapi.common.textmanager;

import com.itsradiix.foundationapi.common.manager.CommonManager;
import com.itsradiix.foundationapi.common.playerinfo.PlayerInfo;
import com.itsradiix.foundationapi.common.textmanager.languages.Language;
import com.itsradiix.foundationapi.common.textmanager.languages.providers.LanguageProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Locale;
import java.util.Map;

import static com.itsradiix.foundationapi.common.textmanager.TextCreator.parseObjectToString;

@SuppressWarnings("unused")
public class MessagesManager implements CommonManager {
    private static MessagesManager instance;
    private static Map<Locale, Language> languages;
    private static Locale defaultLocale;

    private LanguageProvider provider;
    private final ComponentLogger componentLogger;

    public MessagesManager(ComponentLogger componentLogger, LanguageProvider provider, Locale defaultLocale){
        if (instance != null) throw new RuntimeException("Instance of MessagesManager already exists");
        this.provider = provider;
        this.componentLogger = componentLogger;

        MessagesManager.instance = this;
        MessagesManager.defaultLocale = defaultLocale;
        MessagesManager.languages = provider.getLanguagesByLocale();
    }

    public Component getMessage(Locale locale, String message) {
        return TextCreator.create(getRawMessage(locale, message));
    }

    public Component getMessage(Locale locale, String message, PlayerInfo player, TagResolver... tagResolvers) {
        return TextCreator.create(getRawMessage(locale, message), player, tagResolvers);
    }

    public Component getMessage(Locale locale, String message, TagResolver... tagResolvers) {
        return TextCreator.create(getRawMessage(locale, message), null, tagResolvers);
    }

    public String getRawMessage(Locale locale, String message) {
        try {
            Language language = languages.get(locale);
            Object object = language.messages().get(message);
            if (object == null) object = getLanguage(defaultLocale).messages().get(message);
            return parseObjectToString(object);
        } catch (Exception e){
            componentLogger.warn("Cannot get message with key: {}", message);
            return "{" + message + "}";
        }
    }

    public static Language getLanguage(Locale locale){
        if (languages.containsKey(locale)){
            return languages.get(locale);
        }
        return languages.get(defaultLocale);
    }

    public static MessagesManager getInstance() {
        return instance;
    }

    public void reload(){
        languages.clear();
        languages = provider.getLanguagesByLocale();
    }

    public void setProvider(LanguageProvider provider) {
        this.provider = provider;
        reload();
    }

    public void updateMessage(Language language, String messageKey, String message) {
        language.messages().put(messageKey, message);
    }

    public ComponentLogger getComponentLogger() {
        return componentLogger;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
