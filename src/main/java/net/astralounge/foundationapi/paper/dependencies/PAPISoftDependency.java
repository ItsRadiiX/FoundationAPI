package net.astralounge.foundationapi.paper.dependencies;

import net.astralounge.foundationapi.common.dependencies.InstancedPluginDependency;

public class PAPISoftDependency<T> extends InstancedPluginDependency<T> {
    public PAPISoftDependency(Class<T> classType) {
        super("PlaceholderAPI", Level.SOFT_PLUGIN, classType);
    }
}
