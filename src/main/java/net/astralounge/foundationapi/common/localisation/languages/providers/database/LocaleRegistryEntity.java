package net.astralounge.foundationapi.common.localisation.languages.providers.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Keeps track of which locales have their own messages table.
 * <p>
 * table: foundation_message_locales
 * column: localeTag (e.g. "en_UK")
 */
@DatabaseTable(tableName = "foundation_message_locales")
public class LocaleRegistryEntity {

    @DatabaseField(id = true, canBeNull = false)
    private String localeTag;

    public LocaleRegistryEntity() {
        // ORMLite needs a no-arg constructor
    }

    public LocaleRegistryEntity(String localeTag) {
        this.localeTag = localeTag;
    }

    public String getLocaleTag() {
        return localeTag;
    }

    public void setLocaleTag(String localeTag) {
        this.localeTag = localeTag;
    }
}