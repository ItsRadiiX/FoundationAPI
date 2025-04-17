package com.itsradiix.foundationapi.paper.database;

import com.itsradiix.foundationapi.common.datamanagement.files.converter.YAMLConverter;
import com.itsradiix.foundationapi.common.datamanagement.files.handlers.ConfigurationHandler;
import com.itsradiix.foundationapi.common.manager.CommonManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class DatabaseManager implements CommonManager {

    private ConnectionSource connectionSource;

    private ConfigurationHandler databaseConfiguration;
    private final String path;

    public DatabaseManager(String path) {
        this.path = path;
    }

    @Override
    public void onLoad() throws Exception {
        databaseConfiguration = new ConfigurationHandler(path, new YAMLConverter<>(), true);
        connectionSource = new JdbcPooledConnectionSource(getUrl());
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        connectionSource.closeQuietly();
    }

    public String getUrl() {
        switch (getDataBaseType()) {
            case SQLITE -> { return String.format("jdbc:sqlite:%s", getDatabase()); }
            case MYSQL -> { return String.format("jdbc:mysql://%s:%s/%s", getHost(), getPort(), getDatabase());}
            case POSTGRES -> { return String.format("jdbc:postgresql://%s:%s/%s", getHost(), getPort(), getDatabase());}

            // Default using H2 as this is the most lightweight
            default -> {
                return String.format("jdbc:h2:%s", getDatabase());
            }
        }
    }

    private String getPassword() {
        return databaseConfiguration.getString("host", "");
    }

    private String getUsername() {
        return databaseConfiguration.getString("username", "root");
    }

    private String getDatabase() {
        return databaseConfiguration.getString("database", "foundationAPI");
    }

    private int getPort() {
        return databaseConfiguration.getInteger("host", 3306);
    }

    private String getHost() {
        return databaseConfiguration.getString("host", "localhost");
    }

    private DatabaseType getDataBaseType() {
        return DatabaseType.valueOf(databaseConfiguration.getString("databaseType", "H2").toUpperCase());
    }

    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    public enum DatabaseType {
        MYSQL,
        SQLITE,
        POSTGRES,
        H2
    }

}
