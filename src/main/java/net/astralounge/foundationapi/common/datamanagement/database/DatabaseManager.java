package net.astralounge.foundationapi.common.datamanagement.database;

import net.astralounge.foundationapi.common.datamanagement.database.configuration.DataBaseConfiguration;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.AutoReloadableConfigurationHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.implementation.ConfigurationHandler;
import net.astralounge.foundationapi.common.datamanagement.files.manager.FileManager;
import net.astralounge.foundationapi.common.logger.service.FoundationLoggerService;
import net.astralounge.foundationapi.common.manager.CommonManager;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager extends CommonManager {

    private final AutoReloadableConfigurationHandler<DataBaseConfiguration> databaseConfiguration;

    public DatabaseManager(FoundationDefaultPlugin<?> plugin) {
        super(plugin);
        this.databaseConfiguration = new AutoReloadableConfigurationHandler<>(ConfigurationHandler.builder(plugin,
                        DataBaseConfiguration.class,
                        "configuration/database.conf"),
                true);

    }

    @Override
    public void onLoad() throws Exception {
        databaseConfiguration.load();
    }

    @Override
    public void onEnable() throws SQLException {

        if (FoundationLoggerService.getPluginLogger(getPlugin()).isManagerInDebug(getClass().getSimpleName())) {
            // Silence ORMLite unless debugging
            Logger.getLogger("com.j256.ormlite").setLevel(Level.WARNING);
        } else {
            Logger.getLogger("com.j256.ormlite").setLevel(Level.INFO);
        }

        String url = getUrl();

        // Expose the single shared ConnectionSource via the global service
        DatabaseService.getInstance().setConnectionSource(new JdbcPooledConnectionSource(url));
    }

    @Override
    public void onDisable() {
        if (getConnectionSource() == null) return;
        try {
            getConnectionSource().close();
        } catch (Exception e) {
            getPlugin().getFoundationPluginLogger()
                    .warn("Failed to close database connection source during shutdown", e);
        } finally {
            DatabaseService.getInstance().clear();
            DatabaseService.getInstance().setConnectionSource(null);
        }
    }

    @Override
    public Collection<Class<? extends CommonManager>> getCommonDependencies() {
        return List.of(FileManager.class);
    }

    public @Nullable ConnectionSource getConnectionSource() {
        return DatabaseService.getInstance().getConnectionSource();
    }


    public String getUrl() {
        switch (getDataBaseType()) {
            case SQLITE -> {
                return String.format("jdbc:sqlite:%s", getDatabase());
            }
            case MYSQL -> {
                return String.format("jdbc:mysql://%s:%s/%s", getHost(), getPort(), getDatabase());
            }
            case POSTGRES -> {
                return String.format("jdbc:postgresql://%s:%s/%s", getHost(), getPort(), getDatabase());
            }

            // Default using H2 as this is the most lightweight
            default -> {
                File dbDir = new File(getPlugin().getDataFolder(), "database");
                if (!dbDir.exists() && !dbDir.mkdirs()) {
                    getPlugin().getFoundationPluginLogger()
                            .warn("Failed to create database directory at {}", dbDir.getAbsolutePath());
                }
                File dbFileBase = new File(dbDir, getDatabase());
                // H2 will create files like <name>.mv.db under this base path
                return "jdbc:h2:file:" + dbFileBase.getAbsolutePath();
            }
        }
    }

    public DatabaseType getDataBaseType() {
        return databaseConfiguration.getConfigurationObject().databaseType;
    }

    public String getDatabase() {
        return databaseConfiguration.getConfigurationObject().database;
    }

    public String getHost() {
        return databaseConfiguration.getConfigurationObject().host;
    }

    public int getPort() {
        return databaseConfiguration.getConfigurationObject().port;
    }

    public String getUsername() {
        return databaseConfiguration.getConfigurationObject().username;
    }

    public String getPassword() {
        return databaseConfiguration.getConfigurationObject().password;
    }

    public enum DatabaseType {
        MYSQL,
        SQLITE,
        POSTGRES,
        H2
    }

}
