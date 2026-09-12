package net.astralounge.foundationapi.common.playerdata.datastore;

import net.astralounge.foundationapi.common.playerdata.models.FoundationPlayer;

import java.util.UUID;

/**
 * Abstraction over core player data persistence.
 * <p>
 * Implementations are expected to be SQL-backed (e.g. ORMLite).
 */
public interface PlayerDataStore {

    /**
     * Load data for a single player into the given {@link FoundationPlayer}.
     * Implementations should:
     *  - fetch by player.getUuid()
     *  - populate core fields
     */
    void load(FoundationPlayer player) throws Exception;

    /**
     * Save data for a single player.
     */
    void save(FoundationPlayer player) throws Exception;

    /**
     * Optional: delete all persisted data for this player.
     */
    default void delete(UUID uuid) throws Exception {
        // no-op by default
    }
}