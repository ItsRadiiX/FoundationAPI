package nl.bryansuk.foundationapi.common.playerinfo.providers;

import nl.bryansuk.foundationapi.common.filemanager.converter.YAMLConverter;
import nl.bryansuk.foundationapi.common.filemanager.handlers.FolderHandler;

import nl.bryansuk.foundationapi.common.playerinfo.PlayerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInfoFileProvider implements PlayerInfoProvider {

    // TODO implement saving files asynchronously & on update event

    private final FolderHandler<PlayerInfo> playerInfoFolderHandler;
    private Map<UUID, PlayerInfo> playerInfoMap;

    public PlayerInfoFileProvider(String folderPath) {
        this.playerInfoFolderHandler = new FolderHandler<>(folderPath, new YAMLConverter<>(), true);
    }

    @Override
    public Map<UUID, PlayerInfo> getPlayerInfoMap() {
        if (playerInfoMap == null) {
            playerInfoMap = new HashMap<>();
            for (PlayerInfo playerInfo : playerInfoFolderHandler.getObjects()) {
                playerInfoMap.put(playerInfo.getUuid(), playerInfo);
            }
        }
        return playerInfoMap;
    }

    @Override
    public void addPlayer(@NotNull PlayerInfo playerInfo) {
        playerInfoMap.put(playerInfo.getUuid(), playerInfo);
    }

    @Override
    public void addPlayer(@NotNull UUID uuid) {
        if (playerInfoMap.containsKey(uuid)) return;
        addPlayer(new PlayerInfo(uuid));
    }

    @Override
    public void removePlayer(@Nullable UUID uuid) {
        if (uuid != null) playerInfoMap.remove(uuid);
    }

    @Override
    public @Nullable PlayerInfo getPlayerInfo(@Nullable UUID uuid) {
        if (uuid == null) return null;
        if (playerInfoMap.containsKey(uuid)){
            return playerInfoMap.get(uuid);
        }

        addPlayer(uuid);
        return playerInfoMap.get(uuid);
    }

}
