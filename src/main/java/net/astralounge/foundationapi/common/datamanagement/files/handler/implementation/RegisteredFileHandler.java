package net.astralounge.foundationapi.common.datamanagement.files.handler.implementation;

import net.astralounge.foundationapi.common.datamanagement.files.FileManagerService;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;

public class RegisteredFileHandler<T> extends SimpleFileHandler<T> {

    public RegisteredFileHandler(SimpleFileHandler.Builder<T> builder, FoundationDefaultPlugin<?> plugin) {
        super(plugin, builder);
        FileManagerService.getInstance().addHandler(plugin, this);
    }
}
