package net.astralounge.foundationapi.common.datamanagement.files.manager;

import com.google.gson.Gson;
import net.astralounge.foundationapi.common.datamanagement.files.FileManagerService;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.Handler;
import net.astralounge.foundationapi.common.manager.CommonManager;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Per-plugin view over the single global file manager.
 */
public class FileManager extends CommonManager {

    private final FileManagerService registry;
    private final Gson gson;

    public FileManager(FoundationDefaultPlugin<?> foundationDefaultPlugin, Gson gson) {
        super(foundationDefaultPlugin);
        this.gson = gson;
        this.registry = FileManagerService.getInstance();
        this.registry.registerPlugin(foundationDefaultPlugin);
    }

    @Override
    public void onLoad() throws Exception {
        registry.onLoad(getPlugin());
    }

    @Override
    public void onEnable() throws Exception {
        registry.onEnable(getPlugin());
    }

    @Override
    public void onDisable() throws Exception {
        registry.onDisable(getPlugin());
    }

    @Override
    public Collection<Class<? extends CommonManager>> getCommonDependencies() {
        return Collections.emptyList();
    }

    /* ---------------------------------------------------------------------
     * Convenience wrappers for plugin-specific operations
     * Only use this when you are CERTAIN an instance of FileManager exist -> preferably after onEnable()
     * Using this anywhere before may result in race conditions with FileManagerService.
     * --------------------------------------------------------------------- */

    public List<Handler> getAutoReloadingHandlers() {
        return registry.getAutoReloadingHandlers(getPlugin());
    }

    public List<Handler> getReloadableHandlers() {
        return registry.getReloadableHandlers(getPlugin());
    }

    public List<Handler> getHandlers() {
        return registry.getHandlers(getPlugin());
    }

    public @Nullable Handler getHandler(String path) {
        return registry.getHandler(getPlugin(), path);
    }

    public void addHandler(Handler handler) {
        registry.addHandler(getPlugin(), handler);
    }

    public Gson getGson() {
        return gson;
    }

    public ScheduledExecutorService getExecutor() {
        return registry.getExecutor(getPlugin());
    }
}
