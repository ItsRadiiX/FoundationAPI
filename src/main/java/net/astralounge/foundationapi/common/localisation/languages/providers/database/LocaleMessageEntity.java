package net.astralounge.foundationapi.common.localisation.languages.providers.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Single row in a per-locale messages table.
 * Table name will be overridden at runtime (e.g. "foundation_messages_en_uk").
 */
@DatabaseTable(tableName = "foundation_messages_locale")
public class LocaleMessageEntity {

    @DatabaseField(id = true)
    private String messageKey; // e.g. "foundation:generic.no-permission"

    @DatabaseField(canBeNull = false)
    private String message; // MiniMessage/legacy text

    public LocaleMessageEntity() {
        // ORMLite needs a no-arg constructor
    }

    public LocaleMessageEntity(String messageKey, String message) {
        this.messageKey = messageKey;
        this.message = message;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
