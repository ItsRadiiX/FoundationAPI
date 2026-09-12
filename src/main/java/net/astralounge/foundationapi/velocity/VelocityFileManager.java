package net.astralounge.foundationapi.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.io.InputStream;
import java.nio.file.Path;

public class VelocityFileManager {

    private final ProxyServer proxyServer;
    private final Path dataDirectory;

    public VelocityFileManager(ProxyServer proxyServer, Path dataDirectory, ComponentLogger logger) {
        this.proxyServer = proxyServer;
        this.dataDirectory = dataDirectory;
    }

    public InputStream getDefaultResource(String path) {
        try {
            return getClass().getClassLoader().getResourceAsStream(path);
        } catch (Exception e) {
            return null;
        }
    }
}
