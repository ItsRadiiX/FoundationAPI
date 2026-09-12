package net.astralounge.foundationapi.common.datamanagement.files;

import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.AutoReloadableHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.Handler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.ReadWriteHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.ReloadableHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.models.ReloadResult;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Single global file manager that stores all handlers per plugin.
 */
public final class FileManagerService {

    private static final FileManagerService INSTANCE = new FileManagerService();

    private final Map<String, PluginFileContext> pluginContexts = new ConcurrentHashMap<>();

    private FileManagerService() {}

    public static FileManagerService getInstance() {
        return INSTANCE;
    }

    private PluginFileContext getOrCreateContext(FoundationDefaultPlugin<?> plugin) {
        return pluginContexts.computeIfAbsent(
                plugin.getClass().getSimpleName(),
                k -> new PluginFileContext(plugin));
    }

    /* ---------------------------------------------------------------------
     * Registration
     * --------------------------------------------------------------------- */

    public PluginFileContext registerPlugin(FoundationDefaultPlugin<?> plugin) {
        return getOrCreateContext(plugin);
    }

    public void unregisterPlugin(FoundationDefaultPlugin<?> plugin) {
        PluginFileContext context = pluginContexts.remove(plugin.getClass().getSimpleName());
        if (context == null) return;

        stopAutoReloading(context);

        context.handlers.sort(Comparator.comparingInt(Handler::getPriority));

        // Persist and disable handlers, then shut down executor
        for (Handler handler : context.handlers) {
            if (handler.isEnabled()) {
                if (handler instanceof ReadWriteHandler readWriteHandler) {
                    readWriteHandler.disable();
                    readWriteHandler.destroyHandler();
                } else {
                    handler.disable();
                }
            }
        }

        context.executor.shutdown();
        try {
            context.executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            context.handlers.clear();
        }
    }

    public void addHandler(FoundationDefaultPlugin<?> plugin, Handler handler) {
        getOrCreateContext(plugin).handlers.add(handler);
    }

    public List<Handler> getHandlers(FoundationDefaultPlugin<?> plugin) {
        return new ArrayList<>(getOrCreateContext(plugin).handlers);
    }

    public List<Handler> getAutoReloadingHandlers(FoundationDefaultPlugin<?> plugin) {
        return getOrCreateContext(plugin).handlers.stream()
                .filter(AutoReloadableHandler.class::isInstance)
                .toList();
    }

    public List<Handler> getReloadableHandlers(FoundationDefaultPlugin<?> plugin) {
        return getOrCreateContext(plugin).handlers.stream()
                .filter(ReloadableHandler.class::isInstance)
                .toList();
    }

    public Handler getHandler(FoundationDefaultPlugin<?> plugin, String path) {
        return getOrCreateContext(plugin).handlers.stream()
                .filter(handler -> handler.getPath().equals(path))
                .findFirst()
                .orElse(null);
    }

    public ScheduledExecutorService getExecutor(FoundationDefaultPlugin<?> plugin) {
        return getOrCreateContext(plugin).executor;
    }

    /* ---------------------------------------------------------------------
     * Lifecycle hooks (called from per-plugin FileManager)
     * --------------------------------------------------------------------- */

    public void onLoad(FoundationDefaultPlugin<?> plugin) throws Exception {
        for (Handler handler : getOrCreateContext(plugin).handlers) {
            handler.load();
        }
    }

    public void onEnable(FoundationDefaultPlugin<?> plugin) {
        PluginFileContext context = getOrCreateContext(plugin);

        context.handlers.sort(Comparator.comparingInt(Handler::getPriority).reversed());

        for (Handler handler : context.handlers) {
            if (handler.isEnabled()) continue;
            handler.enable();
        }

        plugin.getFoundationPluginLogger().debug("{} file handlers have been enabled.", context.handlers.size());
        plugin.getFoundationPluginLogger().debug("{}", context.handlers);

        startAutoReloading(context, plugin.getAutoReloadTime(), plugin.isAutoReloadEnabled());
        plugin.getFoundationPluginLogger().debug("Auto-reload enabled with a time of {} ticks.", plugin.getAutoReloadTime());
    }

    public void onDisable(FoundationDefaultPlugin<?> plugin) {
        unregisterPlugin(plugin);
    }

    /* ---------------------------------------------------------------------
     * Auto-reload
     * --------------------------------------------------------------------- */

    private Runnable createAutoReloadTask(PluginFileContext context) {
        return () -> {
            for (Handler handler : context.handlers) {
                if (!(handler instanceof AutoReloadableHandler autoReloadableHandler)) continue;

                if (autoReloadableHandler.isAutoReloadEnabled()) {
                    ReloadResult result = autoReloadableHandler.reload();
                    switch (result) {
                        case ERROR_READING_FILE -> context.plugin.getFoundationPluginLogger()
                                .error("Error while reloading {}", handler.getFile().getName());
                        case READ_NEW_FILE -> context.plugin.getFoundationPluginLogger()
                                .debug("Reloaded: {}", handler.getFile().getName());
                        case NO_UPDATE -> {
                            if (context.plugin.getFoundationPluginLogger().getDebugNoFileChanges()) {
                                context.plugin.getFoundationPluginLogger()
                                        .debug("No changes detected for: {}", handler.getFile().getName());
                            }
                        }
                        case null, default -> {
                        }
                    }
                }
            }
        };
    }

    public void startAutoReloading(FoundationDefaultPlugin<?> plugin, int autoReloadTimeSeconds, boolean enabled) {
        PluginFileContext context = getOrCreateContext(plugin);
        startAutoReloading(context, autoReloadTimeSeconds, enabled);
    }

    private void startAutoReloading(PluginFileContext context, int autoReloadTimeSeconds, boolean enabled) {
        if (!enabled) {
            stopAutoReloading(context);
            return;
        }

        if (!context.startedAutoReloading) {
            context.scheduledAutoReloadTask = context.executor.scheduleAtFixedRate(
                    createAutoReloadTask(context),
                    autoReloadTimeSeconds,
                    autoReloadTimeSeconds,
                    TimeUnit.SECONDS
            );
            context.startedAutoReloading = true;
        }
    }

    public void stopAutoReloading(FoundationDefaultPlugin<?> plugin) {
        PluginFileContext context = getOrCreateContext(plugin);
        stopAutoReloading(context);
    }

    private void stopAutoReloading(PluginFileContext context) {
        if (context.startedAutoReloading) {
            if (context.scheduledAutoReloadTask != null) {
                context.scheduledAutoReloadTask.cancel(false);
                context.scheduledAutoReloadTask = null;
            }
            context.startedAutoReloading = false;
        }
    }

    public static final class PluginFileContext {
        final FoundationDefaultPlugin<?> plugin;
        final ScheduledExecutorService executor;
        final List<Handler> handlers;

        ScheduledFuture<?> scheduledAutoReloadTask;
        boolean startedAutoReloading;

        PluginFileContext(FoundationDefaultPlugin<?> plugin) {
            this.plugin = plugin;
            this.executor = Executors.newScheduledThreadPool(2, Thread.ofVirtual().factory());
            this.handlers = new ArrayList<>();
        }
    }
}
