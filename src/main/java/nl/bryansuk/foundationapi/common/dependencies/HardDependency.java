package nl.bryansuk.foundationapi.common.dependencies;

import nl.bryansuk.foundationapi.paper.plugin.FoundationPaperPlugin;
import nl.bryansuk.foundationapi.common.startup.LoadError;
import nl.bryansuk.foundationapi.common.startup.PluginStartupData;

import java.lang.reflect.InvocationTargetException;

public record HardDependency<T>(String name, FoundationPaperPlugin plugin, Class<T> classType) implements Dependency<T>{

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T createInstance() {
        try {
            return classType.getConstructor(FoundationPaperPlugin.class).newInstance(plugin);
        } catch (InstantiationException |
                 IllegalAccessException |
                 NoSuchMethodException |
                 InvocationTargetException e) {
            PluginStartupData.getInstance().addLoadError(new LoadError(LoadError.Level.FATAL, "Unable to load dependency: " + name));
            return null;
        }
    }

}
