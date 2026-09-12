package net.astralounge.foundationapi.common.playerdata.models;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.astralounge.foundationapi.common.logger.service.FoundationLoggerService;
import com.j256.ormlite.dao.Dao;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.UUID;

/**
 * Base class for modules that manage per-player plugin data models.
 * <p>
 * Provides:
 * - A Caffeine cache of M keyed by UUID.
 * - Default load/save/evict hooks, wired to ORMLite Dao<M, UUID>.
 * <p>
 * Typical plugin usage:
 * - Subclass PlayerDataModule<PlayerTagProfile>.
 * - Implement createNewModel(UUID) to build a default profile.
 * - Register the module in PlayerDataService.registerModule(...).
 */
public abstract class PlayerDataModule<M> {

    protected final Dao<M, UUID> dao;
    protected final Cache<UUID, M> cache;

    protected PlayerDataModule(Dao<M, UUID> dao) {
        this(dao, defaultCacheBuilder().build());
    }

    protected PlayerDataModule(Dao<M, UUID> dao, Cache<UUID, M> cache) {
        this.dao = dao;
        this.cache = cache;
    }

    /**
     * Default cache configuration for plugin models.
     * Plugins can supply their own Cache instance via the other constructor.
     */
    protected static Caffeine<Object, Object> defaultCacheBuilder() {
        return Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(30));
    }

    /**
     * Create a new default model instance for the given player.
     * Called when no row exists in the DB yet.
     */
    protected abstract M createNewModel(UUID playerUuid) throws Exception;

    /**
     * Get or create the model for this player (cached + persisted).
     */
    public @Nullable M getOrCreate(UUID playerUuid) {
        return cache.get(playerUuid, id -> {
            try {
                M fromDb = dao.queryForId(id);
                if (fromDb != null) return fromDb;

                M created = createNewModel(id);
                dao.create(created);
                return created;
            } catch (Exception exception) {
                FoundationLoggerService.getGlobalLogger()
                        .error(String.format("Failed to load/create %s for %s",
                                        this.getClass().getSimpleName(), playerUuid),
                                exception);
                return null;
            }
        });
    }

    /**
     * Get the cached model, or null if not loaded.
     */
    public M getIfCached(UUID playerUuid) {
        return cache.getIfPresent(playerUuid);
    }

    /**
     * Called by PlayerDataService after a FoundationPlayer has been loaded and updated.
     * Default behaviour: ensure the model is in memory (lazy DB load/create).
     */
    public void onPlayerLoaded(FoundationPlayer foundationPlayer) {
        try {
            getOrCreate(foundationPlayer.getUuid());
        } catch (Exception exception) {
            FoundationLoggerService.getGlobalLogger()
                    .error(String.format("Failed to load %s for player %s",
                                    this.getClass().getSimpleName(), foundationPlayer.getUuid()),
                            exception);
        }
    }

    /**
     * Called by PlayerDataService before a FoundationPlayer is saved.
     * Default behavior: if a model is cached, persist it via Dao.
     */
    public void onPlayerSaving(FoundationPlayer foundationPlayer) {
        UUID uuid = foundationPlayer.getUuid();
        M model = cache.getIfPresent(uuid);
        if (model != null) {
            try {
                dao.createOrUpdate(model);
            } catch (Exception exception) {
                FoundationLoggerService.getGlobalLogger()
                        .error(String.format("Failed to save %s for player %s",
                                        this.getClass().getSimpleName(), foundationPlayer.getUuid()),
                                exception);
            }
        }
    }

    /**
     * Called by PlayerDataService when the FoundationPlayer is evicted from its cache.
     * Default behaviour: invalidate this module's cached model as well.
     */
    public void onPlayerEvicted(UUID playerUuid) {
        cache.invalidate(playerUuid);
    }
}