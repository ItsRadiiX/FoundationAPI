package net.astralounge.foundationapi.common.dependencies;

import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;

public class InstancedPluginDependency<T> extends PluginDependency {

    protected Class<T> classType;
    protected T instance;

    public InstancedPluginDependency(String name, Level level, Class<T> classType) {
        super(name, level);
        this.classType = classType;
    }

    public Class<T> getClassType() {
        return classType;
    }

    public T getInstance() {
        return instance;
    }

    public void createInstance(FoundationDefaultPlugin<?> plugin) throws Throwable {
        if (classType == null) return;
        instance = classType.getConstructor(FoundationDefaultPlugin.class).newInstance(plugin);
    }

}
