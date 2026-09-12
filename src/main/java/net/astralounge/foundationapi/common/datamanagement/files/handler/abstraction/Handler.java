package net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction;

import net.astralounge.foundationapi.common.logger.loggers.FoundationLogger;
import net.astralounge.foundationapi.common.logger.service.FoundationLoggerService;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

public abstract class Handler implements Comparable<Handler> {
    protected @Nullable FoundationDefaultPlugin<?> plugin;
    protected @Nullable FoundationLogger logger;

    protected final String path;
    protected final Path pluginFolderPath;
    private FileTime locallyLastModified;
    private boolean enabled;
    private boolean instantEnable;
    private int priority = 0;

    protected abstract void handlerLoadLogic() throws Exception;

    protected abstract void handlerEnableLogic() throws Exception;

    protected abstract void handlerDisableLogic() throws Exception;

    private @Nullable Runnable preLoadLogic;
    private @Nullable Runnable postLoadLogic;
    private @Nullable Runnable preEnableLogic;
    private @Nullable Runnable postEnableLogic;
    private @Nullable Runnable preDisableLogic;
    private @Nullable Runnable postDisableLogic;

    public Handler(File pluginFolderPath, String path, boolean instantEnable) {
        this.path = path;
        this.pluginFolderPath = pluginFolderPath.toPath().resolve(path);
        this.instantEnable = instantEnable;
    }

    public Handler(FoundationDefaultPlugin<?> plugin, String path, boolean instantEnable) {
        this(plugin.getDataFolder(), path, instantEnable);
        this.plugin = plugin;
    }

    public void instantEnable(boolean instantEnable) {
        this.instantEnable = instantEnable;
        if (instantEnable) {
            load();
            enable();
        }
        this.instantEnable = false;
    }

    public void load() {
        try {
            runPreLoadLogic();
            handlerLoadLogic();
            runPostLoadLogic();
        } catch (Exception exception) {
            getLogger(plugin).error("[Handler] (Error) '{}' could not be loaded!", path, exception);
        }
    }

    public void enable() {
        try {
            if (!enabled || (instantEnable)) {
                setEnabled(true);
                getLogger(plugin).debug("[Handler] (Enabling) - '{}'", path);
                runPreEnableLogic();
                handlerEnableLogic();
                runPostEnableLogic();
                getLogger(plugin).debug("[Handler] (Enabled) - '{}'", path);
            } else {
                getLogger(plugin).warn("[Handler] (Warning) - '{}' is already enabled!", path);
            }
        } catch (Exception exception) {
            getLogger(plugin).error("[Handler] (Error) - '{}' could not be enabled!", path, exception);
        }
    }

    public void disable() {
        try {
            if (enabled) {
                getLogger(plugin).debug("[Handler] (Disabling) - '{}'", path);
                runPreDisableLogic();
                handlerDisableLogic();
                runPostDisableLogic();
                getLogger(plugin).debug("[Handler] (Disabled) - '{}'", path);
            } else {
                getLogger(plugin).warn("[Handler] (Warning) - '{}' is already disabled!", path);
            }
        } catch (Exception exception) {
            getLogger(plugin).error("[Handler] (Error) - '{}' could not be disabled!", path, exception);
        } finally {
            instantEnable = false;
            setEnabled(false);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected boolean returnIfNotEnabled() {
        if (isEnabled()) {
            return false;
        } else {
            getLogger(plugin).error("[Handler] (Error) - '{}' ConfigurationHandler is not enabled!", path);
            return true;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public File getFile() {
        return pluginFolderPath.toFile();
    }

    public Path getPluginFolderPath() {
        return pluginFolderPath;
    }

    protected String getFolderSeparator() {
        return File.separator;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    private @Nullable FileTime getFileLastModified() {
        try {
            if (Files.exists(pluginFolderPath)) {
                return Files.getLastModifiedTime(pluginFolderPath);
            }
        } catch (IOException ignored) {}
        return null;
    }

    protected void updateLocallyLastModified() {
        locallyLastModified = getFileLastModified();
    }

    public boolean isResourceFound() {
        return Files.exists(pluginFolderPath);
    }

    public boolean isNewVersionAvailable() {
        FileTime currentTime = getFileLastModified();
        // If both are null, the file is still missing (no change)
        // If one is null and the other isn't, the file was either created or deleted (change!)
        // If both exist, compare their timestamps
        return !Objects.equals(locallyLastModified, currentTime);
    }

    public String getPath() {
        return path;
    }

    protected static Path moveToOld(Path configPath) throws IOException {
        Path oldPath = configPath.resolveSibling(configPath.getFileName() + ".old." + System.currentTimeMillis());

        // Avoid overwriting an existing .old file
        if (Files.exists(oldPath)) {
            oldPath = configPath.resolveSibling(
                    configPath.getFileName() + ".old." + System.currentTimeMillis()
            );
        }

        Files.move(
                configPath,
                oldPath,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
        );

        return oldPath;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "path='" + path + '\'' + '}';
    }

    protected FoundationLogger getLogger(@Nullable FoundationDefaultPlugin<?> plugin) {
        if (logger == null) {
            return FoundationLoggerService.get(plugin);
        }
        return logger;
    }

    public void runPreLoadLogic() {
        if (preLoadLogic != null) {
            preLoadLogic.run();
        }
    }

    public void setPreLoadLogic(@Nullable Runnable preLoadLogic) {
        this.preLoadLogic = preLoadLogic;
    }

    private void runPostLoadLogic() {
        if (postLoadLogic != null) {
            postLoadLogic.run();
        }
    }

    public void setPostLoadLogic(@Nullable Runnable postLoadLogic) {
        this.postLoadLogic = postLoadLogic;
    }

    private void runPreEnableLogic() {
        if (preEnableLogic != null) {
            preEnableLogic.run();
        }
    }

    public void setPreEnableLogic(@Nullable Runnable preEnableLogic) {
        this.preEnableLogic = preEnableLogic;
    }

    private void runPostEnableLogic() {
        if (postEnableLogic != null) {
            postEnableLogic.run();
        }
    }

    public void setPostEnableLogic(@Nullable Runnable postEnableLogic) {
        this.postEnableLogic = postEnableLogic;
    }

    private void runPreDisableLogic() {
        if (preDisableLogic != null) {
            preDisableLogic.run();
        }
    }

    public void setPreDisableLogic(@Nullable Runnable preDisableLogic) {
        this.preDisableLogic = preDisableLogic;
    }

    private void runPostDisableLogic() {
        if (postDisableLogic != null) {
            postDisableLogic.run();
        }
    }

    public void setPostDisableLogic(@Nullable Runnable postDisableLogic) {
        this.postDisableLogic = postDisableLogic;
    }

    @Override
    public int compareTo(@NonNull Handler other) {
        return Integer.compare(this.priority, other.priority);
    }
}
