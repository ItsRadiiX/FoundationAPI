package nl.bryansuk.foundationapi.dependencies;

import nl.bryansuk.foundationapi.plugin.FoundationPlugin;
import nl.bryansuk.foundationapi.startup.LoadError;
import nl.bryansuk.foundationapi.startup.PluginStartupData;

import java.lang.reflect.InvocationTargetException;

public record SoftDependency<T>(String name, FoundationPlugin plugin, Class<T> classType) implements Dependency<T> {

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T createInstance() {
        try {
            return classType.getConstructor(FoundationPlugin.class).newInstance(plugin);
        } catch (InstantiationException |
                 IllegalAccessException |
                 NoSuchMethodException |
                 InvocationTargetException e) {
            PluginStartupData.getInstance().addLoadError(new LoadError(LoadError.Level.RISK, "Unable to load dependency: " + name));
            return null;
        }
    }

}
