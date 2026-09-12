package net.astralounge.foundationapi.common.internalmessaging.messages;

import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.models.ReloadResult;
import net.astralounge.foundationapi.common.internalmessaging.InternalMessageHandler;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class FileReloadMessage extends InternalMessage {
    private static final InternalMessageHandler handlers = new InternalMessageHandler();
    private final Path path;
    private final ReloadResult reloadResult;

    public FileReloadMessage(Path path, ReloadResult result) {
        this.path = path;
        this.reloadResult = result;
    }

    public ReloadResult getReloadResult() {
        return reloadResult;
    }

    public Path getPath() {
        return path;
    }

    public @NotNull InternalMessageHandler getHandlers() {
        return handlers;
    }

    public static InternalMessageHandler getHandlerList() {
        return handlers;
    }
}
