package net.astralounge.foundationapi.paper.playerdata;

import net.astralounge.foundationapi.common.playerdata.managers.PlayerDataManager;
import net.astralounge.foundationapi.paper.plugin.FoundationPaperPlugin;

public class PaperPlayerDataManager extends PlayerDataManager {

    public PaperPlayerDataManager(FoundationPaperPlugin<?,?> plugin) {
        super(plugin);
    }

    @Override
    protected void registerListeners() {
        getPlugin().getServer().getPluginManager().registerEvents(new PlayerDataListener(), getPlugin());
    }

    @Override
    protected void registerAllOnlinePlayers() {
        getPlugin().getServer().getOnlinePlayers().forEach(player ->
                playerDataService.registerOnlinePlayer(
                        player.getUniqueId(),
                        player.getName()
        ));
    }

    @Override
    public FoundationPaperPlugin<?,?> getPlugin() {
        return (FoundationPaperPlugin<?,?>) super.getPlugin();
    }
}
