package nl.bryansuk.foundationapi.common.playerinfo;

import nl.bryansuk.foundationapi.common.playerinfo.providers.PlayerInfoProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.UUID;

public class PlayerInfoManager {
    private static PlayerInfoManager instance;
    private final PlayerInfoProvider provider;

    public PlayerInfoManager(PlayerInfoProvider provider) {
        instance = this;
        this.provider = provider;
    }

    public void addPlayerInfo(PlayerInfo playerInfo){
        provider.addPlayer(playerInfo);
    }

    public void addPlayer(UUID uuid){
        provider.addPlayer(uuid);
    }

    public void removePlayerInfo(UUID uuid){
        provider.removePlayer(uuid);
    }

    public @Nullable PlayerInfo getPlayerInfo(UUID uuid){
        return provider.getPlayerInfo(uuid);
    }

    public void savePlayerInfo(PlayerInfo playerInfo){
        provider.savePlayerInfo(playerInfo);
    }

    public void saveAllPlayerInfo(){
        provider.saveAllPlayerInfo();
    }

    public static PlayerInfoManager getInstance(){
        return instance;
    }

}
