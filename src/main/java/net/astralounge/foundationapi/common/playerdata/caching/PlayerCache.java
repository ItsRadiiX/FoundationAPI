package net.astralounge.foundationapi.common.playerdata.caching;

import net.astralounge.foundationapi.common.playerdata.models.FoundationPlayer;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

// Java
public interface PlayerCache {

    FoundationPlayer getOrCreate(UUID uuid, Function<UUID, FoundationPlayer> factory);

    FoundationPlayer getIfPresent(UUID uuid);

    Collection<FoundationPlayer> values();

    void invalidate(UUID uuid);

    void invalidateAll();

    void cleanup(); // no-op for Redis, used by Caffeine
}
