package com.itsradiix.foundationapi.paper.filemanager;

import com.itsradiix.foundationapi.common.datamanagement.files.FileManager;
import com.itsradiix.foundationapi.paper.events.FileReloadEvent;
import com.itsradiix.foundationapi.paper.events.FolderReloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;

public class PaperFileManager extends FileManager {

    private final JavaPlugin javaPlugin;

    public PaperFileManager(JavaPlugin javaPlugin) {
        super(javaPlugin.getComponentLogger());
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

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        shutdown();
    }
}
