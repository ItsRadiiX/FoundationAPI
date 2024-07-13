package com.itsradiix.foundationapi.velocity.filemanager;

import com.itsradiix.foundationapi.common.datamanagement.files.FileManager;
import com.itsradiix.foundationapi.velocity.events.FileReloadEvent;
import com.itsradiix.foundationapi.velocity.events.FolderReloadEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.io.InputStream;
import java.nio.file.Path;

public class VelocityFileManager extends FileManager {

    private final ProxyServer proxyServer;
    private final Path dataDirectory;

    public VelocityFileManager(ProxyServer proxyServer, Path dataDirectory, ComponentLogger logger) {
        super(logger);
        this.proxyServer = proxyServer;
        this.dataDirectory = dataDirectory;
    }

    @Override
    public InputStream getDefaultResource(String path) {
        try {
            return getClass().getClassLoader().getResourceAsStream(path);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void callFileReloadEvent(String fileName) {
        proxyServer.getEventManager().fire(new FileReloadEvent(fileName));
    }

    @Override
    public void callFolderReloadEvent(String folderName) {
        proxyServer.getEventManager().fire(new FolderReloadEvent(folderName));
    }

    @Override
    public String getDataFolder() {
        return dataDirectory.getFileName().toString();
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
