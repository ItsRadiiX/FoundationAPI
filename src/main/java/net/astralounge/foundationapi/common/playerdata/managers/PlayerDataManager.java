package net.astralounge.foundationapi.common.playerdata.managers;

import net.astralounge.foundationapi.common.datamanagement.database.DatabaseManager;
import net.astralounge.foundationapi.common.datamanagement.database.DatabaseService;
import net.astralounge.foundationapi.common.datamanagement.files.manager.FileManager;
import net.astralounge.foundationapi.common.manager.CommonManager;
import net.astralounge.foundationapi.common.playerdata.datastore.ORMLitePlayerDataStore;
import net.astralounge.foundationapi.common.playerdata.services.PlayerDataService;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import com.j256.ormlite.support.ConnectionSource;

import java.util.Collection;
import java.util.List;

public abstract class PlayerDataManager extends CommonManager {

    protected final PlayerDataService playerDataService;

    public PlayerDataManager(FoundationDefaultPlugin<?> plugin) {
        super(plugin);
        this.playerDataService = PlayerDataService.getInstance();
    }

    protected abstract void registerListeners();
    protected abstract void registerAllOnlinePlayers();

    @Override
    protected void onLoad() throws Exception {
    }

    @Override
    protected void onEnable() throws Exception {
        ConnectionSource cs = DatabaseService.getInstance().getConnectionSource();
        if (cs == null) {
            getPlugin().getFoundationPluginLogger()
                    .error("PlayerDataManager enabled but no ConnectionSource is available");
            return;
        }

        // Example store that uses ORMLite; adjust to your concrete implementation
        playerDataService.setStore(new ORMLitePlayerDataStore(cs));

        registerListeners();
        registerAllOnlinePlayers();
    }

    @Override
    protected void onDisable() throws Exception {
        PlayerDataService.getInstance().saveAll();
        // Optional: clear the store reference
        playerDataService.setStore(null);
    }

    @Override
    public Collection<Class<? extends CommonManager>> getCommonDependencies() {
        return List.of(FileManager.class, DatabaseManager.class);
    }

}
