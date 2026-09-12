package net.astralounge.foundationapi.common.datamanagement.database.configuration;

import net.astralounge.foundationapi.common.datamanagement.database.DatabaseManager;
import net.astralounge.foundationapi.common.datamanagement.files.configuration.AutoReloadableConfiguration;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class DataBaseConfiguration extends AutoReloadableConfiguration {

    @Comment( "The host of the database")
    public String host = "localhost";

    @Comment("The port of the database")
    public int port = 3306;

    @Comment("The database name")
    public String database = "foundationAPI";

    @Comment("The password of the database")
    public String password = "";

    @Comment("The username of the database")
    public String username = "root";

    @Comment("The type of database")
    public DatabaseManager.DatabaseType databaseType = DatabaseManager.DatabaseType.H2;

}
