package net.astralounge.foundationapi.common.playerdata.services;

import net.astralounge.foundationapi.common.logger.service.FoundationLoggerService;
import net.astralounge.foundationapi.common.playerdata.caching.CaffeinePlayerCache;
import net.astralounge.foundationapi.common.playerdata.caching.PlayerCache;
import net.astralounge.foundationapi.common.playerdata.datastore.PlayerDataStore;
import net.astralounge.foundationapi.common.playerdata.models.FoundationPlayer;
import net.astralounge.foundationapi.common.playerdata.models.PlayerDataModule;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central cache and access layer for FoundationPlayer objects.
 * <p>
 * - Caches players in memory via Caffeine.
 * - Delegates persistence to a configured PlayerDataStore.
 * - Notifies registered PlayerDataModules so plugins can cache/load/save
 *   their own models in sync with core player data.
 */
public final class PlayerDataService {

    private static final PlayerDataService INSTANCE = new PlayerDataService();

    public static PlayerDataService getInstance() { return INSTANCE; }

    private PlayerDataService() {
        this.cache = CaffeinePlayerCache.getDefaultInstance(this);
    }

    private final PlayerCache cache;

    /**
     * Backing store (SQL via ORMLite). Must be set during plugin startup.
     */
    private PlayerDataStore store;

    /**
     * Registered modules that participate in player lifecycle.
     */
    private final List<PlayerDataModule<?>> modules = new CopyOnWriteArrayList<>();

    public void setStore(PlayerDataStore store) {
        this.store = store;
    }

    public void registerModule(PlayerDataModule<?> module) {
        modules.add(module);
    }

    public void unregisterModule(PlayerDataModule<?> module) {
        modules.remove(module);
    }

    public List<PlayerDataModule<?>> getModules() {
        return modules;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public FoundationPlayer getOrCreate(UUID uuid) {
        return cache.getOrCreate(uuid, FoundationPlayer::new);
    }

    public FoundationPlayer get(UUID uuid) {
        return cache.getIfPresent(uuid);
    }

    public Collection<FoundationPlayer> getAllPlayers() {
        return cache.values();
    }

    /**
     * Called when a player joins.
     * - Ensures a FoundationPlayer exists in cache.
     * - Loads persistent core data.
     * - Updates basic metadata (name, timestamps).
     * - Notifies modules that the player is loaded.
     */
    public FoundationPlayer registerOnlinePlayer(UUID uuid, String lastKnownName) {
        long now = System.currentTimeMillis();

        FoundationPlayer player = getOrCreate(uuid);

        // Load from DB (if any)
        loadPlayerFromStorage(player);

        if (player.getFirstJoin() == 0L) {
            player.setFirstJoin(now);
        }

        player.setLastKnownName(lastKnownName);
        player.setLastSeen(now);

        // Notify modules
        for (PlayerDataModule<?> module : modules) {
            module.onPlayerLoaded(player);
        }

        return player;
    }

    /**
     * Saves a single player (e.g. on quit).
     */
    public void save(UUID uuid) {
        FoundationPlayer player = get(uuid);
        if (player != null) {
            // Modules first
            for (PlayerDataModule<?> module : modules) {
                module.onPlayerSaving(player);
            }
            savePlayerToStorage(player);
        }
    }

    /**
     * Saves all currently cached players (e.g. on plugin disable).
     */
    public void saveAll() {
        for (FoundationPlayer player : cache.values()) {
            for (PlayerDataModule<?> module : modules) {
                module.onPlayerSaving(player);
            }
            savePlayerToStorage(player);
        }
    }

    /**
     * Optional manual maintenance; Caffeine also does its own.
     */
    public void runMaintenance() {
        cache.cleanup();
    }

    public void invalidate(UUID uuid) {
        cache.invalidate(uuid);
    }

    // -------------------------------------------------------------------------
    // Storage delegation
    // -------------------------------------------------------------------------

    public void loadPlayerFromStorage(FoundationPlayer player) {
        if (store == null) return;
        try {
            store.load(player);
        } catch (Exception e) {
            FoundationLoggerService.getGlobalLogger()
                    .error("Failed to load player data for " + player.getUuid(), e);
        }
    }

    public void savePlayerToStorage(FoundationPlayer player) {
        if (store == null) return;
        try {
            store.save(player);
        } catch (Exception e) {
            FoundationLoggerService.getGlobalLogger()
                    .error("Failed to save player data for " + player.getUuid(), e);
        }
    }
}