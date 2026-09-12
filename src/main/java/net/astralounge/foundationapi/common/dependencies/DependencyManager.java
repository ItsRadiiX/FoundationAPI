package net.astralounge.foundationapi.common.dependencies;

import net.astralounge.foundationapi.common.manager.CommonManager;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import net.astralounge.foundationapi.common.startup.LoadError;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.List;

public class DependencyManager extends CommonManager {

    boolean dependenciesLoaded = false;

    public DependencyManager(FoundationDefaultPlugin<?> plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() throws Exception {}

    @Override
    protected void onEnable() throws Exception {}

    @Override
    protected void onDisable() throws Exception {}

    @Override
    public Collection<Class<? extends CommonManager>> getCommonDependencies() {
        return List.of();
    }

    public void checkDependencies() {
        List<PluginDependency> dependencies = getPlugin().getPluginDependencies();

        int totalRequiredDependencies;

        boolean ignoreSoftDependencies = getPlugin().getStartupManager().getStartupShowSoftDependencyNotFound();

        if (ignoreSoftDependencies) {
            totalRequiredDependencies = dependencies.size();
        } else {
            totalRequiredDependencies = dependencies.stream()
                    .filter(pluginDependency ->
                            pluginDependency.getLevel() == PluginDependency.Level.CRITICAL_PLUGIN)
                    .toList()
                    .size();
        }

        int requiredDependenciesFound = 0;

        for (PluginDependency pluginDependency : dependencies) {
            boolean dependencyFound = Bukkit.getPluginManager().getPlugin(pluginDependency.getName()) != null;
            boolean isSoftDependency = pluginDependency.getLevel() == PluginDependency.Level.SOFT_PLUGIN;

            if (dependencyFound) {
                try {
                    if (pluginDependency instanceof InstancedPluginDependency<?> instancedPluginDependency) {
                        instancedPluginDependency.createInstance(getPlugin());
                    }
                    pluginDependency.setLoaded(true);
                    getPlugin().getStartupManager().logDependencyStatus(pluginDependency.getName(), true);
                    if (ignoreSoftDependencies || !isSoftDependency) {
                        requiredDependenciesFound++;
                    }
                } catch (Throwable e) {
                    pluginDependency.setLoaded(false);
                    getPlugin().getFoundationPluginLogger().error(e.getMessage());
                    getPlugin().getStartupManager().addLoadError(new LoadError(pluginDependency.getLevel().getLoadErrorLevel(),
                            String.format("<red>Could not load dependency</red> <gold%s</gold>", pluginDependency.getName())));
                }
            } else {
                pluginDependency.setLoaded(false);
                if (ignoreSoftDependencies && !isSoftDependency) {
                    getPlugin().getStartupManager().logDependencyStatus(pluginDependency.getName(), false);
                    getPlugin().getStartupManager().addLoadError(new LoadError(pluginDependency.getLevel().getLoadErrorLevel(),
                            String.format("<red>Could not find dependency</red> <gold%s</gold>", pluginDependency.getName())));
                }
            }
        }

        if (requiredDependenciesFound != totalRequiredDependencies) {
            getPlugin().getStartupManager().printStartupReport(String.format("<red>%-41s<gray>|", "Could not find all dependencies!"));
            getPlugin().getStartupManager().printStartupReport(String.format("<red>%-41s<gray>|", "These plugins are required!"));
        } else {
            dependenciesLoaded = true;
        }
    }

    public boolean areDependenciesLoaded() {
        return dependenciesLoaded;
    }
}
