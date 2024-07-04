package nl.bryansuk.foundationapi.common.filemanager;

import nl.bryansuk.foundationapi.common.exceptions.FileManagerException;
import nl.bryansuk.foundationapi.common.filemanager.handlers.FileHandler;
import nl.bryansuk.foundationapi.common.filemanager.handlers.Handler;
import nl.bryansuk.foundationapi.common.logging.FoundationLogger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public abstract class FileManager {

    protected static FileManager instance;

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4, Thread.ofVirtual().factory());
    private static ScheduledFuture<?> scheduledAutoReloadTask;
    protected static boolean startedAutoReloading = false;

    protected static final List<Handler> handlers = Collections.synchronizedList(new ArrayList<>());

    protected final FoundationLogger logger;

    public abstract InputStream getDefaultResource(String path);
    public abstract void callFileReloadEvent(String fileName);
    public abstract void callFolderReloadEvent(String folderName);
    public abstract String getDataFolder();

    public FileManager(FoundationLogger logger) {
        if(instance != null) {
            throw new FileManagerException("You can only have one instance of the FileManager at a time.");
        }
        FileManager.instance = this;

        this.logger = logger;
    }

    public void shutdown() throws InterruptedException {
        stopAutoReloading();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        for (Handler handler : handlers) {
            if (handler instanceof FileHandler) ((FileHandler<?>) handler).write();
            handler.destroy();
        }
    }

    public static FileManager getInstance() {
        if (instance == null) throw new FileManagerException("FileManager has not yet been initialized.");
        return instance;
    }

    public static FoundationLogger getLogger() {
        return getInstance().logger;
    }

    /*
        A U T O     R E L O A D I N G    M E T H O D S
     */

    /**
     * Starts auto-reloading of files with the specified time interval.
     *
     * @param autoReloadManagerTime the time interval in ticks
     */
    public static boolean startAutoReloading(int autoReloadManagerTime, boolean autoReload){
        if (!autoReload) {
            stopAutoReloading();
            return false;
        }

        if (!startedAutoReloading) {
            scheduledAutoReloadTask = executor.scheduleAtFixedRate(getAutoReloadTask(),
                    autoReloadManagerTime, autoReloadManagerTime, TimeUnit.SECONDS);
            startedAutoReloading = true;
        }
        return true;
    }

    /**
     * Stops auto-reloading of files.
     */
    public static void stopAutoReloading(){
        if (startedAutoReloading) {
            if (scheduledAutoReloadTask!= null) {
                scheduledAutoReloadTask.cancel(false);
                scheduledAutoReloadTask = null;
            }
            startedAutoReloading = false;
        }
    }

    public static Runnable getAutoReloadTask(){
            return () -> {
                for (Handler handler : getAutoReloadingHandlers()) {
                    if (handler instanceof FileHandler && handler.onReload()) {
                        getLogger().debug("Reloaded: {}", handler.getFile().getName());
                    }
                }
            };
    }

    public static void addHandler(Handler handler) {
        if (!containsHandler(handler)) {
            handlers.add(handler);
        }
    }

    public static void removeHandler(Handler handler) {
        handlers.remove(handler);
    }

    public static boolean containsHandler(Handler handler) {
        return handlers.contains(handler);
    }

    public static List<Handler> getAutoReloadingHandlers() {
        return handlers.stream().filter(Handler::isAutoReloading).toList();
    }

    public static List<Handler> getHandlers(){
        return handlers;
    }

    public static ExecutorService getExecutor() {
        return executor;
    }
}
