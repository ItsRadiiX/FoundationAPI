package net.astralounge.foundationapi.common.plugin.configuration;

import net.astralounge.foundationapi.common.datamanagement.files.configuration.AutoReloadableConfiguration;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class GlobalConfiguration extends AutoReloadableConfiguration {

    @Comment("Auto Reload Configuration")
    public AutoReloadSection autoReload;

    public GlobalConfiguration() {
        this.autoReload = new AutoReloadSection();
    }

    @ConfigSerializable
    public static class AutoReloadSection {
        @Comment("Enable to auto reload global configuration")
        public boolean globalAutoReloadEnabled;

        @Comment("The time in seconds between each global auto reload")
        public int globalAutoReloadTime;

        public AutoReloadSection() {
            this.globalAutoReloadEnabled = true;
            this.globalAutoReloadTime = 10;
        }
    }
}
