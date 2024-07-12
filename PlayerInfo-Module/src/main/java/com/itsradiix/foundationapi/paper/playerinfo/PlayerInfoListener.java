package com.itsradiix.foundationapi.paper.playerinfo;

import com.itsradiix.foundationapi.common.playerinfo.PlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;


public class PlayerInfoListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setPlayerName(playerInfo.getPlayerName());
        playerInfo.setUuid(uuid);

    }

}
