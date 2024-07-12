package com.itsradiix.foundationapi.paper.events;

import com.itsradiix.foundationapi.common.playerinfo.PlayerInfo;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerInfoUpdate extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final PlayerInfo playerInfo;

    public PlayerInfoUpdate(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
