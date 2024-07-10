package nl.bryansuk.foundationapi.paper.playerinfo;

import nl.bryansuk.foundationapi.common.datamanagement.database.DatabaseManager;
import nl.bryansuk.foundationapi.common.playerinfo.PlayerInfo;
import nl.bryansuk.foundationapi.common.playerinfo.PlayerInfoDAOImpl;
import nl.bryansuk.foundationapi.paper.plugin.FoundationPaperPlugin;
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

        DatabaseManager.getPlayerInfoDAO().save(playerInfo);
    }

}
