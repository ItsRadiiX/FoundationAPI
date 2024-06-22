package nl.bryansuk.foundationapi.components;

import io.papermc.paper.command.brigadier.Commands;
import nl.bryansuk.foundationapi.logging.FoundationLogger;
import nl.bryansuk.foundationapi.plugin.FoundationPlugin;

public abstract class FoundationComponent {

    protected FoundationPlugin plugin;
    protected FoundationLogger logger;
    protected String name;

    public FoundationComponent(FoundationPlugin plugin, FoundationLogger logger, String name) {
        this.plugin = plugin;
        this.logger = logger;
        this.name = name;
    }

    public FoundationComponent(FoundationPlugin plugin, FoundationLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.name = getClass().getSimpleName();
    }

    public abstract void onComponentEnable() throws Exception;
    public abstract void onComponentDisable() throws Exception;
    public abstract void registerCommands(Commands commands);

    public void start(){
        try {
            onComponentEnable();
            logger.logToConsole("&7Component \"" + name + "\" has been activated!");
        } catch (Exception e) {
            logger.errorToConsole("&cComponent \"" + name + "\" was not activated!");
            logger.errorToConsole(e.getMessage(), e);
        }
    }

    public void reload() throws Exception {
        onComponentEnable();
        onComponentDisable();
    }

    public void stop(){
        try {
            onComponentDisable();
            logger.logToConsole("&7Component \"" + name + "\" has been deactivated!");
        } catch (Exception e) {
            logger.errorToConsole("&cAn error occurred while deactivating Component \"" + name + "\"!");
            logger.errorToConsole(e.getMessage(), e);
        }
    }

}
