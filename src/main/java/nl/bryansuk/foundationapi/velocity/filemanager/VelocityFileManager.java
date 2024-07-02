package nl.bryansuk.foundationapi.velocity.filemanager;

import com.velocitypowered.api.proxy.ProxyServer;
import nl.bryansuk.foundationapi.common.filemanager.FileManager;
import nl.bryansuk.foundationapi.velocity.events.FileReloadEvent;
import nl.bryansuk.foundationapi.velocity.events.FolderReloadEvent;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Path;

public class VelocityFileManager extends FileManager {

    private final ProxyServer proxyServer;
    private final Path dataDirectory;

    public VelocityFileManager(ProxyServer proxyServer, Path dataDirectory, Logger logger) {
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
}
