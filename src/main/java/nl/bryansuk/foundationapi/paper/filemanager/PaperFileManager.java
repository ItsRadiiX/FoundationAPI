package nl.bryansuk.foundationapi.paper.filemanager;

import nl.bryansuk.foundationapi.common.filemanager.FileManager;
import nl.bryansuk.foundationapi.common.logging.FoundationLogger;
import nl.bryansuk.foundationapi.paper.events.FileReloadEvent;
import nl.bryansuk.foundationapi.paper.events.FolderReloadEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;

public class PaperFileManager extends FileManager {

    private final JavaPlugin javaPlugin;

    public PaperFileManager(JavaPlugin javaPlugin, FoundationLogger logger) {
        super(logger);
        this.javaPlugin = javaPlugin;
    }

    @Override
    public InputStream getDefaultResource(String path) {
        return javaPlugin.getResource(path);
    }

    @Override
    public void callFileReloadEvent(String fileName) {
        javaPlugin.getServer().getPluginManager().callEvent(new FileReloadEvent(fileName));
    }

    @Override
    public void callFolderReloadEvent(String folderName) {
        javaPlugin.getServer().getPluginManager().callEvent(new FolderReloadEvent(folderName));
    }

    @Override
    public String getDataFolder() {
        return javaPlugin.getDataFolder().getPath();
    }
}
