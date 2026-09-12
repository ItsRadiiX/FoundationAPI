package net.astralounge.foundationapi.common.localisation.languages.providers.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Single row in the messages table.
 *
 * Composite key: (localeTag, messageKey).
 */
@DatabaseTable(tableName = "foundation_messages")
public class MessageEntity {

    @DatabaseField(id = true)
    private String compositeId; // localeTag + "|" + messageKey

    @DatabaseField(canBeNull = false)
    private String localeTag; // e.g. "en_UK"

    @DatabaseField(canBeNull = false)
    private String messageKey; // e.g. "foundation:generic.no-permission"

    @DatabaseField(canBeNull = false)
    private String message; // MiniMessage/legacy text

    public MessageEntity() {
        // ORMLite needs a no-arg constructor
    }

    public MessageEntity(String localeTag, String messageKey, String message) {
        this.localeTag = localeTag;
        this.messageKey = messageKey;
        this.message = message;
        this.compositeId = localeTag + "|" + messageKey;
    }

    public String getLocaleTag() {
        return localeTag;
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