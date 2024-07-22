package com.itsradiix.foundationapi.common.playerinfo;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "player_info")
public class PlayerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID uuid;

    @Column(name = "player_name")
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
