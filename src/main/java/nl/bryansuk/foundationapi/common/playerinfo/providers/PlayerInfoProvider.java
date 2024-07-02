package nl.bryansuk.foundationapi.common.playerinfo.providers;

import nl.bryansuk.foundationapi.common.playerinfo.PlayerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public interface PlayerInfoProvider {
    Map<UUID, PlayerInfo> getPlayerInfoMap();
    void addPlayer(@NotNull PlayerInfo playerInfo);
    void addPlayer(@NotNull UUID uuid);
    void removePlayer(@Nullable UUID uuid);
    @Nullable PlayerInfo getPlayerInfo(@Nullable UUID uuid);
}
