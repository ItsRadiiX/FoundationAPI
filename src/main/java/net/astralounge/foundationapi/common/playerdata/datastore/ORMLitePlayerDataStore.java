package net.astralounge.foundationapi.common.playerdata.datastore;

import net.astralounge.foundationapi.common.playerdata.models.FoundationPlayer;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.UUID;

/**
 * SQL-backed player data store using ORMLite.
 * <p>
 * Uses FoundationPlayer itself as the ORMLite entity.
 */
public final class ORMLitePlayerDataStore implements PlayerDataStore {

    private final Dao<FoundationPlayer, UUID> dao;

    public ORMLitePlayerDataStore(ConnectionSource connectionSource) throws Exception {
        this.dao = DaoManager.createDao(connectionSource, FoundationPlayer.class);
        TableUtils.createTableIfNotExists(connectionSource, FoundationPlayer.class);
    }

    @Override
    public void load(FoundationPlayer target) throws Exception {
        UUID uuid = target.getUuid();
        FoundationPlayer fromDb = dao.queryForId(uuid);
        if (fromDb == null) {
            // New player; nothing to copy
            return;
        }

        // Copy fields from the DB entity into the cached instance
        target.setLastKnownName(fromDb.getLastKnownName());
        target.setFirstJoin(fromDb.getFirstJoin());
        target.setLastSeen(fromDb.getLastSeen());
        target.setCustomLocale(fromDb.getCustomLocale());
    }

    @Override
    public void save(FoundationPlayer player) throws Exception {
        dao.createOrUpdate(player);
    }

    @Override
    public void delete(UUID uuid) throws Exception {
        dao.deleteById(uuid);
    }
}