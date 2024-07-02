package nl.bryansuk.foundationapi.common.filemanager;

import nl.bryansuk.foundationapi.common.exceptions.FileManagerException;
import nl.bryansuk.foundationapi.common.filemanager.handlers.Handler;
import nl.bryansuk.foundationapi.common.logging.FoundationLogger;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public abstract class FileManager {

    protected static FileManager instance;

    protected final FoundationLogger logger;

    protected static final List<Handler> handlers = Collections.synchronizedList(new ArrayList<>());
    protected static Runnable autoReloadTask;
    protected static boolean startedAutoReloading = false;

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
            autoReloadTask = getAutoReloadTask(autoReloadManagerTime);
            startedAutoReloading = true;
        }
        return true;
    }

    /**
     * Stops auto-reloading of files.
     */
    public static void stopAutoReloading(){
        if (startedAutoReloading) {
            if (autoReloadTask!= null) {
                autoReloadTask = null;
            }
            startedAutoReloading = false;
        }
    }

    public static Runnable getAutoReloadTask(int autoReloadManagerTime){
            return () -> {
                for (Handler handler : getAutoReloadingHandlers()) {
                    if (handler.onReload()) {
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



}
