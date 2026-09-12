package net.astralounge.foundationapi.common.playerdata.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Locale;
import java.util.UUID;

/**
 * Foundation-owned player model.
 * <p>
 * - One row per player in the "players" table.
 * - Contains only core, cross-plugin fields.
 * - Plugin-specific data should live in separate tables linked by this UUID.
 */
@DatabaseTable(tableName = "foundation_players")
public final class FoundationPlayer {

    // --- Identity & core data ---

    @DatabaseField(id = true, canBeNull = false, columnName = "player_uuid")
    private UUID uuid;

    @DatabaseField
    private String lastKnownName;

    @DatabaseField
    private long firstJoin;  // epoch millis

    @DatabaseField
    private long lastSeen;   // epoch millis

    /**
     * Custom locale preference for this player, if any.
     * Stored as a language tag string (e.g. "en-US") using getter/setter helpers.
     */
    @DatabaseField(columnName = "locale")
    private String customLocaleTag;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Constructor used by your code when creating a new player in memory.
     */
    public FoundationPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * No-arg constructor required by ORMLite.
     * Should not be used directly in normal code.
     */
    @SuppressWarnings("unused")
    public FoundationPlayer() {
        // for ORMLite
    }

    // -------------------------------------------------------------------------
    // Core getters/setters
    // -------------------------------------------------------------------------

    public UUID getUuid() {
        return uuid;
    }

    public String getLastKnownName() {
        return lastKnownName;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
    }

    public long getFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(long firstJoin) {
        this.firstJoin = firstJoin;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Locale getCustomLocale() {
        return customLocaleTag == null ? null : Locale.forLanguageTag(customLocaleTag);
    }

    public void setCustomLocale(Locale customLocale) {
        this.customLocaleTag = (customLocale == null) ? null : customLocale.toLanguageTag();
    }
}