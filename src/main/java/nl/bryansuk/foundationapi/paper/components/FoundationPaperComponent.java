package nl.bryansuk.foundationapi.paper.components;


import io.papermc.paper.command.brigadier.Commands;
import nl.bryansuk.foundationapi.common.logging.FoundationLogger;
import nl.bryansuk.foundationapi.paper.plugin.FoundationPaperPlugin;

@SuppressWarnings("UnstableApiUsage")
public abstract class FoundationPaperComponent {

    protected FoundationPaperPlugin plugin;
    protected FoundationLogger logger;
    protected String name;

    public FoundationPaperComponent(FoundationPaperPlugin plugin, FoundationLogger logger, String name) {
        this.plugin = plugin;
        this.logger = logger;
        this.name = name;
    }

    public FoundationPaperComponent(FoundationPaperPlugin plugin, FoundationLogger logger) {
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
            logger.debug("&7Component \"" + name + "\" has been activated!");
        } catch (Exception e) {
            logger.error("&cComponent \"" + name + "\" was not activated!");
            logger.error(e);
        }
    }

    public void reload() throws Exception {
        onComponentEnable();
        onComponentDisable();
    }

    public void stop(){
        try {
            onComponentDisable();
            logger.debug("&7Component \"" + name + "\" has been deactivated!");
        } catch (Exception e) {
            logger.error("&cAn error occurred while deactivating Component \"" + name + "\"!");
            logger.error(e);
        }
    }

}