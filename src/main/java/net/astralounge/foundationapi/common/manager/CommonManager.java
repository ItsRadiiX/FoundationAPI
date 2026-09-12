package net.astralounge.foundationapi.common.manager;

import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;

import java.util.Collection;

public abstract class CommonManager {

    private final FoundationDefaultPlugin<?> plugin;
    private boolean enabled;

    protected abstract void onLoad() throws Exception;
    protected abstract void onEnable() throws Exception;
    protected abstract void onDisable() throws Exception;
    public abstract Collection<Class<? extends CommonManager>> getCommonDependencies();

    public CommonManager(FoundationDefaultPlugin<?> plugin) {
        this.plugin = plugin;
        this.enabled = false;
    }

    public void load() {
        try {
            onLoad();
        } catch (Exception e) {
            plugin.getFoundationPluginLogger().error("Failed to load common manager!", e);
        }
    }

    public void enable() {
        try {
            onEnable();
            enabled = true;
        } catch (Exception e) {
            plugin.getFoundationPluginLogger().error("Failed to enable common manager!", e);
            disable();
        }
    }

    public void disable() {
        try {
            onDisable();
        } catch (Exception e) {
            plugin.getFoundationPluginLogger().error("Failed to disable common manager!", e);
        } finally {
            enabled = false;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public FoundationDefaultPlugin<?> getPlugin() {
        return plugin;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
