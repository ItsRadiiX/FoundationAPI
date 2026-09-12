package net.astralounge.foundationapi.paper.playerdata;

import net.astralounge.foundationapi.common.playerdata.services.PlayerDataService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerDataListener implements Listener {

    private final PlayerDataService playerDataService = PlayerDataService.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player bukkitPlayer = event.getPlayer();
        playerDataService.registerOnlinePlayer(
                bukkitPlayer.getUniqueId(),
                bukkitPlayer.getName()
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player bukkitPlayer = event.getPlayer();
        playerDataService.save(bukkitPlayer.getUniqueId());
    }
}
