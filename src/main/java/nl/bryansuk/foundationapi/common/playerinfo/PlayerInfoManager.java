package nl.bryansuk.foundationapi.common.playerinfo;

import nl.bryansuk.foundationapi.common.playerinfo.providers.PlayerInfoProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class PlayerInfoManager {
    private final PlayerInfoProvider provider;

    public PlayerInfoManager(PlayerInfoProvider provider) {
        this.provider = provider;
    }

    public void addPlayerInfo(PlayerInfo playerInfo){
        provider.addPlayer(playerInfo);
    }

    public void removePlayerInfo(UUID uuid){
        provider.removePlayer(uuid);
    }

    public Map<UUID, PlayerInfo> getPlayerInfoMap(){
        return provider.getPlayerInfoMap();
    }

    public @Nullable PlayerInfo getPlayerInfo(UUID uuid){
        return provider.getPlayerInfo(uuid);
    }
}
