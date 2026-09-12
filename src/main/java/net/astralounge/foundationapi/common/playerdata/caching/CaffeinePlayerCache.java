package net.astralounge.foundationapi.common.playerdata.caching;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import net.astralounge.foundationapi.common.playerdata.models.FoundationPlayer;
import net.astralounge.foundationapi.common.playerdata.models.PlayerDataModule;
import net.astralounge.foundationapi.common.playerdata.services.PlayerDataService;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

// Java
public final class CaffeinePlayerCache implements PlayerCache {

    private final Cache<UUID, FoundationPlayer> delegate;

    public CaffeinePlayerCache(RemovalListener<UUID, FoundationPlayer> removalListener) {
        this.delegate = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(30))
                .removalListener(removalListener)
                .build();
    }

    @Override
    public FoundationPlayer getOrCreate(UUID uuid, Function<UUID, FoundationPlayer> factory) {
        return delegate.get(uuid, factory);
    }

    @Override
    public FoundationPlayer getIfPresent(UUID uuid) {
        return delegate.getIfPresent(uuid);
    }

    @Override
    public Collection<FoundationPlayer> values() {
        return delegate.asMap().values();
    }

    @Override
    public void invalidate(UUID uuid) {
        delegate.invalidate(uuid);
    }

    @Override
    public void invalidateAll() {
        delegate.invalidateAll();
    }

    @Override
    public void cleanup() {
        delegate.cleanUp();
    }

    public static PlayerCache getDefaultInstance(PlayerDataService dataService) {
        return new CaffeinePlayerCache((key, player, cause) -> {
            if (player != null) {
                for (PlayerDataModule<?> module : dataService.getModules()) {
                    module.onPlayerSaving(player);
                }
                dataService.savePlayerToStorage(player);
                for (PlayerDataModule<?> module : dataService.getModules()) {
                    module.onPlayerEvicted(key);
                }
            }
        });
    }
}
