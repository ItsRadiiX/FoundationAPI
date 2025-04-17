package com.itsradiix.foundationapi.paper.plugin;

import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

@SuppressWarnings("UnstableApiUsage")
public abstract class FoundationPaperComponent {

    protected FoundationPaperPlugin plugin;
    protected ComponentLogger logger;
    protected String name;
    protected boolean alwaysEnabled;

    public FoundationPaperComponent(FoundationPaperPlugin plugin, ComponentLogger logger, String name, boolean alwaysEnabled) {
        this.plugin = plugin;
        this.logger = logger;
        this.name = name;
        this.alwaysEnabled = alwaysEnabled;
    }

    public FoundationPaperComponent(FoundationPaperPlugin plugin, ComponentLogger logger, String name) {
        this(plugin, logger, name, false);
    }

    public FoundationPaperComponent(FoundationPaperPlugin plugin, ComponentLogger logger, boolean alwaysEnabled) {
        this(plugin, logger, null, alwaysEnabled);
        name = this.getClass().getSimpleName();
    }

    public FoundationPaperComponent(FoundationPaperPlugin plugin, ComponentLogger logger) {
        this(plugin, logger, false);
    }

    public abstract void onComponentEnable() throws Exception;
    public abstract void onComponentDisable() throws Exception;
    public abstract void registerCommands(Commands commands);

    public void start(){
        try {
            onComponentEnable();
            logger.debug("&7Component \"{}\" has been activated!", name);
        } catch (Exception e) {
            logger.error("&cComponent \"{}\" was not activated!", name);
            logger.error(e.getMessage());
        }
    }

    public void reload() throws Exception {
        onComponentEnable();
        onComponentDisable();
    }

    public void stop(){
        try {
            onComponentDisable();
            logger.debug("&7Component \"{}\" has been deactivated!", name);
        } catch (Exception e) {
            logger.error("&cAn error occurred while deactivating Component \"{}\"!", name);
            logger.error(e.getMessage());
        }
    }

    public String getName() {
        return name;
    }

    public boolean isAlwaysEnabled() {
        return alwaysEnabled;
    }
}
