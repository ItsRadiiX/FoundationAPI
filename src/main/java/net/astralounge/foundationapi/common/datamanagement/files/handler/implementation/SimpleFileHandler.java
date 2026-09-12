package net.astralounge.foundationapi.common.datamanagement.files.handler.implementation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.Handler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.ReadWriteHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.models.ReloadResult;
import net.astralounge.foundationapi.common.logger.loggers.FoundationLogger;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * A simple generic file handler implementation that uses Gson
 * to serialize and deserialize a single object to and from the disk.
 *
 * <p>This handler is intended for lightweight data storage where
 * a single object is persisted as JSON.</p>
 *
 * @param <T> the type of object being handled
 */
public class SimpleFileHandler<T> extends ReadWriteHandler {

    /**
     * The class type of the object being serialized/deserialized.
     * Used by Gson for reflective type resolution.
     */
    private final Type type;

    /**
     * The in-memory representation of the loaded object.
     */
    private volatile T object;

    private final Gson gson;

    protected SimpleFileHandler(Builder<T> builder) {
        super(builder.pluginFolderPath, builder.path, builder.instantEnable, builder.afterReloadConsumer);
        this.logger = builder.logger;
        this.type = builder.type;
        this.gson = builder.gson;
        setPriority(builder.priority);
        instantEnable(builder.instantEnable);
    }

    protected SimpleFileHandler(FoundationDefaultPlugin<?> plugin, Builder<T> builder) {
        super(plugin, builder.path, builder.instantEnable, builder.afterReloadConsumer);
        this.logger = builder.logger;
        this.type = builder.type;
        this.gson = builder.gson;
        setPriority(builder.priority);
        instantEnable(builder.instantEnable);
    }

    /**
     * Creates a builder for {@link SimpleFileHandler}.
     *
     * @param plugin the plugin owning this handler
     * @param type   the JSON type to (de)serialize
     * @param path   relative path within the plugin's data folder
     * @param <T>    the object type
     * @return a new {@link Builder} instance
     */
    public static <T> Builder<T> builder(
            FoundationDefaultPlugin<?> plugin,
            Type type,
            String path
    ) {
        return new Builder<>(plugin, path, type);
    }

    /**
     * Creates a builder for {@link SimpleFileHandler}.
     *
     * @param logger the logger type to use for this handler
     * @param type   the JSON type to (de)serialize
     * @param path   relative path within the plugin's data folder
     * @param <T>    the object type
     * @return a new {@link Builder} instance
     */
    public static <T> Builder<T> builder(
            FoundationLogger logger,
            File pluginFolderPath,
            Type type,
            String path
    ) {
        return new Builder<>(logger, pluginFolderPath,path, type);
    }

    public static final class Builder<T> {

        private final FoundationLogger logger;
        private final File pluginFolderPath;
        private final String path;
        private final Type type;

        private Gson gson;
        private boolean instantEnable = true;
        private int priority = 0;
        private @Nullable Consumer<ReloadResult> afterReloadConsumer;

        private Builder(FoundationDefaultPlugin<?> plugin, String path, Type type) {
            this.logger = plugin.getFoundationPluginLogger();
            this.pluginFolderPath = plugin.getDataFolder();
            this.path = path;
            this.type = type;
            this.gson = plugin.getFileManager().getGson();
        }

        private Builder(FoundationLogger logger, File pluginFolderPath, String path, Type type) {
            this.logger = logger;
            this.pluginFolderPath = pluginFolderPath;
            this.path = path;
            this.type = type;
            this.gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create();
        }

        /**
         * Override the default {@link Gson} instance.
         */
        public Builder<T> gson(Gson gson) {
            this.gson = gson;
            return this;
        }

        /**
         * Whether the handler should be enabled immediately.
         * Default: {@code true}.
         */
        public Builder<T> instantEnable(boolean instantEnable) {
            this.instantEnable = instantEnable;
            return this;
        }

        public Builder<T> priority(int priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Consumer to be called after each reload.
         */
        public Builder<T> afterReloadConsumer(@Nullable Consumer<ReloadResult> afterReloadConsumer) {
            this.afterReloadConsumer = afterReloadConsumer;
            return this;
        }

        /**
         * Builds a new {@link SimpleFileHandler} instance.
         */
        public SimpleFileHandler<T> build() {
            return new SimpleFileHandler<>(this);
        }
    }

    /**
     * Logic executed when the handler is loaded.
     *
     * <p>This implementation does nothing by default but may be
     * overridden in subclasses if needed.</p>
     */
    @Override
    public void handlerLoadLogic() {
        // No-op
    }

    @Override
    public ReloadResult handlerReloadLogic(boolean resourceFound) {
        T backupObject = null;
        if (object != null) {
            String json = gson.toJson(object, type);
            backupObject = gson.fromJson(json, type);
        }

        try {
            if (!resourceFound) throw new FileNotFoundException("Data file missing at " + pluginFolderPath);

            readInternal(false);
            return ReloadResult.READ_NEW_FILE;

        } catch (Exception e) {
            getLogger(plugin).error("Error reloading JSON data! Reverting to memory backup.", e);

            try {
                // During a reload failure, we preserve the user's broken data in .old
                // and write back what we currently have in memory.
                handleCorruption(backupObject);
                return ReloadResult.REVERTED_TO_BACKUP;
            } catch (IOException ioException) {
                getLogger(plugin).error("Critical failure during JSON data restoration!", ioException);
                return ReloadResult.ERROR_READING_FILE;
            }
        }
    }

    @Override
    public void destroyHandler() {
        object = null;
    }

    /**
     * Logic executed when the handler is enabled.
     *
     * <p>This implementation does nothing by default but may be
     * overridden in subclasses if needed.</p>
     */
    @Override
    public void handlerEnableLogic() throws IOException {
        handlerReadLogic();
    }

    /**
     * Reads the file from disk and deserializes its JSON content
     * into an object of type {@code T}.
     *
     * @return {@code true} if the read operation was successful
     * @throws IOException if an I/O error occurs while reading the file
     */
    @Override
    public boolean handlerReadLogic() throws IOException {
        // If the file doesn't exist, we don't treat it as an error,
        // just an empty data state.
        if (Files.notExists(pluginFolderPath)) {
            return true;
        }

        try {
            readInternal(true);
            return true;
        } catch (Exception e) {
            throw new IOException("Failed to read JSON data: " + path, e);
        }
    }

    /**
     * Serializes the current object into JSON and writes it to disk.
     *
     * @throws IOException if an I/O error occurs while writing the file
     */
    @Override
    public void handlerWriteLogic() throws IOException {
        if (object == null) return;
        writeInternal(object);
    }

    /**
     * Logic executed when the handler is disabled.
     *
     * <p>This implementation does nothing by default but may be
     * overridden in subclasses if needed.</p>
     */
    @Override
    public void handlerDisableLogic() {
        // No-op
    }

    private void readInternal(boolean allowRecovery) throws Exception {
        try (Reader reader = Files.newBufferedReader(pluginFolderPath)) {
            getLogger(plugin).debug("Loading JSON data from {}", pluginFolderPath);
            T loaded = gson.fromJson(reader, type);
            if (loaded == null) throw new IOException("JSON resulted in null object");

            this.object = loaded;
            updateLocallyLastModified();
        } catch (Exception e) {
            if (allowRecovery) {
                // On the initial load, corruption moves the file to .old but DOES NOT create defaults.
                // This leaves 'object' as null (or whatever it was), forcing the plugin
                // to handle the missing data gracefully.
                if (Files.exists(pluginFolderPath)) {
                    Path oldPath = moveToOld(pluginFolderPath);
                    getLogger(plugin).warn("Moved corrupted JSON data file to {}", oldPath);
                }
                return;
            }
            throw e;
        }
    }

    private boolean writeInternal(T objectToWrite) throws IOException {
        try (Writer writer = Files.newBufferedWriter(pluginFolderPath)) {
            getLogger(plugin).debug("Writing JSON data to {}", pluginFolderPath);
            gson.toJson(objectToWrite, writer);
            updateLocallyLastModified();
            return true;
        }
    }

    private void handleCorruption(T fallback) throws IOException {
        if (Files.exists(pluginFolderPath)) {
            Path oldPath = Handler.moveToOld(pluginFolderPath);
            getLogger(plugin).warn("Moved broken JSON file to {}", oldPath);
        }
        if (fallback != null) {
            this.object = fallback;
            writeInternal(this.object);
        }
    }

    /**
     * Returns the currently loaded object.
     *
     * @return the loaded object, or {@code null} if not yet read
     */
    public @Nullable T getObject() {
        return object;
    }

    /**
     * Sets the object that will be written to disk.
     *
     * @param object the object to persist
     */
    public void setObject(T object) {
        this.object = object;
    }
}