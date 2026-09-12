package net.astralounge.foundationapi.common.component;

import net.astralounge.foundationapi.common.logger.loggers.FoundationLogger;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FoundationComponent<T extends FoundationDefaultPlugin<T>> {

    private static final Logger log = LoggerFactory.getLogger(FoundationComponent.class);
    protected T plugin;
    protected FoundationLogger logger;
    protected String name;
    protected boolean alwaysEnabled;

    public FoundationComponent(T plugin, String name, boolean alwaysEnabled) {
        this.plugin = plugin;
        this.logger = plugin.getFoundationPluginLogger();
        this.name = name;
        this.alwaysEnabled = alwaysEnabled;
    }

    public FoundationComponent(T plugin, String name) {
        this(plugin, name, false);
    }

    public FoundationComponent(T plugin, boolean alwaysEnabled) {
        this(plugin, null, alwaysEnabled);
        name = this.getClass().getSimpleName();
    }

    public FoundationComponent(T plugin) {
        this(plugin, false);
    }

    public abstract void onComponentEnable() throws Exception;

    public abstract void onComponentDisable() throws Exception;

    public abstract void registerCommands();

    public void start() {
        try {
            onComponentEnable();
            logger.debug("Component '{}' has been activated!", name);
        } catch (Exception e) {
            logger.error("Component '{}' was not activated!", name);
            logger.error(e);
        }
    }

    public void reload() throws Exception {
        onComponentEnable();
        onComponentDisable();
    }

    public void stop() {
        try {
            onComponentDisable();
            logger.debug("Component '{}' has been deactivated!", name);
        } catch (Exception e) {
            logger.error("An error occurred while deactivating Component '{}'!", name);
            logger.error(e);
        }
    }

    public T getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public boolean isAlwaysEnabled() {
        return alwaysEnabled;
    }
}
