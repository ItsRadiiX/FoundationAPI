package net.astralounge.foundationapi.common.localisation.configuration;

import net.astralounge.foundationapi.common.datamanagement.files.configuration.AutoReloadableConfiguration;
import net.astralounge.foundationapi.common.localisation.languages.providers.LanguageProviderType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class MessagesConfiguration extends AutoReloadableConfiguration {

    @Comment("""
            The default language provider to use.
            Defaults to File Language Provider.
            Options: FILES / DATABASE""")
    public LanguageProviderType defaultLanguageProvider = LanguageProviderType.FILES;

    @Comment("""
            The default locale to use.
            Defaults to en_UK as we follow British English spelling.
            For a list of locales: https://simplelocalize.io/data/locales/""")
    public String defaultLocale = "en_UK";

    @Comment("""
            How to store messages in the database when defaultLanguageProvider = DATABASE.
            SINGLE_TABLE  -> all locales in one table (simple behaviour).
            PER_LOCALE    -> one table per locale (e.g. foundation_messages_en_uk, foundation_messages_de_de).
            
            SINGLE_TABLE is recommended for smaller installations with one locale.
            PER_LOCALE is recommended for larger installations with more than one locale.""")
    public DatabaseLayout databaseLayout = DatabaseLayout.PER_LOCALE;

    @Comment("""
            NOTE: THIS VALUE IS NOT MUTABLE AT RUNTIME. A RESTART IS REQUIRED TO CHANGE THIS VALUE.
            How many Locale entries to cache in memory.
            Increase if you have a large number of locales or if you want to reduce database load.
            Defaults to 4""")
    public int databaseCacheSize = 4;

    @Comment("""
            NOTE: THIS VALUE IS NOT MUTABLE AT RUNTIME. A RESTART IS REQUIRED TO CHANGE THIS VALUE.
            How many Locale entries to cache in memory.
            Increase if you have a large number of locales or if you want to reduce database load.
            Defaults to 10 minutes.""")
    public int databaseCacheExpireMinutes = 10;

    @Comment("""
            NOTE: THIS VALUE IS NOT MUTABLE AT RUNTIME. A RESTART IS REQUIRED TO CHANGE THIS VALUE.
            Before regular cache is accessed, we first check if we have already parsed the message before.
            Meaning this cache is accessed more frequently, and thus we probably want messages to persist longer.
            Defaults to 2048.""")
    public int parsedMessagesCacheSize = 2048;

    @Comment("""
            NOTE: THIS VALUE IS NOT MUTABLE AT RUNTIME. A RESTART IS REQUIRED TO CHANGE THIS VALUE.
            Before regular cache is accessed, we first check if we have already parsed the message before.
            Meaning this cache is accessed more frequently, and thus we probably want messages to persist longer.
            How long to cache parsed messages in memory for before they expire.
            Defaults to 120 minutes.""")
    public int parsedMessagesExpireMinutes = 120;

    public enum DatabaseLayout {
        SINGLE_TABLE,
        PER_LOCALE
    }
}
