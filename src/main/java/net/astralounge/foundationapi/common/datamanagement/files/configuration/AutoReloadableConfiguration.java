package net.astralounge.foundationapi.common.datamanagement.files.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class AutoReloadableConfiguration {

    @Comment("Enable to auto reload this configuration")
    public boolean autoReloadConfiguration = true;
}
