package nl.bryansuk.foundationapi.paper.playerinfo;

import nl.bryansuk.foundationapi.common.playerinfo.PlayerInfoManager;
import nl.bryansuk.foundationapi.paper.plugin.FoundationPaperPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;


public class PlayerInfoListener implements Listener {

    private final PlayerInfoManager playerInfoManager = FoundationPaperPlugin.getInstance().getPlayerInfoManager();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!playerInfoManager.hasPlayerInfo(uuid)){
            playerInfoManager.addPlayer(uuid);
        }
    }

}
