package net.astralounge.foundationapi.common.startup.configuration;

import net.astralounge.foundationapi.common.datamanagement.files.configuration.AutoReloadableConfiguration;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;

@ConfigSerializable
public class StartupConfiguration extends AutoReloadableConfiguration {

    @Comment("Enable to show startup report")
    public boolean enableStartupReport= true;

    @Comment("Enable to allow the author of this plugin to be\npromoted throughout the plugin (show some love ♥)")
    public boolean enablePromoteAuthor= true;

    @Comment("Enable to show soft dependency not found message")
    public boolean enableShowSoftDependencyNotFound = true;

    @Comment("What logo to display in startup message")
    public List<String> showLogo = List.of();
}
