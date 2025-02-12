package com.itsradiix.foundationapi.common.playerinfo;

import java.util.UUID;

public class PlayerInfo {

    private UUID uuid;

    private String playerName;

    public PlayerInfo() {}

    public PlayerInfo(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
