package nl.bryansuk.foundationapi.common.playerinfo.providers;

import nl.bryansuk.foundationapi.common.filemanager.converter.JSONConverter;
import nl.bryansuk.foundationapi.common.filemanager.converter.YAMLConverter;
import nl.bryansuk.foundationapi.common.filemanager.handlers.FileHandler;
import nl.bryansuk.foundationapi.common.filemanager.handlers.FolderHandler;
import nl.bryansuk.foundationapi.common.playerinfo.PlayerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInfoFileProvider implements PlayerInfoProvider {
    private final FolderHandler<PlayerInfo> playerInfoFolderHandler;
    private Map<UUID, FileHandler<PlayerInfo>> playerInfoMap;
    private final String folderPath;

    public PlayerInfoFileProvider(String folderPath) {
        this.playerInfoFolderHandler = new FolderHandler<>(folderPath, new YAMLConverter<>(), false, true);
        this.folderPath = folderPath;
        setup();
    }

    private void setup(){
        if (playerInfoMap == null) {
            playerInfoMap = new HashMap<>();

            playerInfoFolderHandler.initialize();

            for (FileHandler<PlayerInfo> fileHandler : playerInfoFolderHandler.getFileHandlersList()){
                PlayerInfo playerInfo = fileHandler.getObject();
                if(playerInfo == null) continue;
                playerInfoMap.put(playerInfo.getUuid(), fileHandler);
            }
        }
    }

    @Override
    public void addPlayer(@NotNull PlayerInfo playerInfo) {
        if (!playerInfoMap.containsKey(playerInfo.getUuid())){
            FileHandler<PlayerInfo> playerInfoFileHandler = new FileHandler<>(
                    (folderPath + "/" + playerInfo.getUuid() + ".json"),
                    new JSONConverter<>(),
                    true,
                    true);
            playerInfoMap.put(playerInfo.getUuid(), playerInfoFileHandler);
            playerInfoFileHandler.write(playerInfo);
        }
    }

    @Override
    public void addPlayer(@NotNull UUID uuid) {
        addPlayer(new PlayerInfo(uuid));
    }

    @Override
    public void removePlayer(@Nullable UUID uuid) {
        if (uuid != null){
            FileHandler<PlayerInfo> fileHandler = playerInfoMap.get(uuid);
            playerInfoMap.remove(uuid);
            fileHandler.destroy();
        }
    }

    @Override
    public boolean containsPlayer(@NotNull UUID uuid) {
        return playerInfoMap.containsKey(uuid);
    }

    @Override
    public @Nullable PlayerInfo getPlayerInfo(@Nullable UUID uuid) {
        if (uuid == null || !playerInfoMap.containsKey(uuid)) return null;
        return playerInfoMap.get(uuid).getObject();
    }

    @Override
    public void savePlayerInfo(PlayerInfo playerInfo) {
        FileHandler<PlayerInfo> fileHandler = playerInfoMap.get(playerInfo.getUuid());
        fileHandler.write();
    }

    @Override
    public void saveAllPlayerInfo() {
        playerInfoMap.values().forEach(FileHandler::write);
    }
}
