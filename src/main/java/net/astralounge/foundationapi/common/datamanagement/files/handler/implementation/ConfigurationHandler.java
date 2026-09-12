package net.astralounge.foundationapi.common.datamanagement.files.handler.implementation;

import net.astralounge.foundationapi.common.datamanagement.files.FileManagerService;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.Handler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.ReadWriteHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.models.ReloadResult;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.SerializerBundle;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.SerializerBundles;
import net.astralounge.foundationapi.common.logger.loggers.FoundationLogger;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A configuration handler backed by a YAML file using the Configurate library.
 *
 * <p>This handler supports automatic reloading and maps the configuration
 * directly to a strongly typed object.</p>
 *
 * @param <T> the configuration object type
 */
public class ConfigurationHandler<T> extends ReadWriteHandler {
    public static <T> Builder<T> builder(FoundationDefaultPlugin<?> plugin, Class<T> type, String path) {
        return new Builder<>(plugin, type, path);
    }

    /**
     * YAML configuration loader responsible for reading and writing the file.
     */
    private final HoconConfigurationLoader loader;

    /**
     * The root configuration node loaded from disk.
     */
    private ConfigurationNode configurationNode;

    /**
     * The class type of the configuration object.
     */
    private final Class<T> classType;

    /**
     * The in-memory representation of the configuration.
     */
    private volatile T configurationObject;

    protected ConfigurationHandler(Builder<T> builder) {
        super(builder.plugin,
                builder.path,
                builder.instantEnable,
                builder.afterReloadConsumer);

        this.classType = builder.classType;
        this.setPriority(builder.priority);

        this.logger = builder.logger;

        this.setPreLoadLogic(builder.preLoadLogic);
        this.setPostLoadLogic(builder.postLoadLogic);
        this.setPreEnableLogic(builder.preEnableLogic);
        this.setPostEnableLogic(builder.postEnableLogic);
        this.setPreDisableLogic(builder.preDisableLogic);
        this.setPostDisableLogic(builder.postDisableLogic);

        // ---- Build serializers ----
        TypeSerializerCollection.Builder serializerBuilder = TypeSerializerCollection.defaults().childBuilder();

        builder.bundles.forEach(b -> b.applyConfigurate(serializerBuilder));

        TypeSerializerCollection serializers = serializerBuilder.build();

        // ---- Build loader ----
        HoconConfigurationLoader.Builder loaderBuilder =
                HoconConfigurationLoader.builder()
                        .path(pluginFolderPath)
                        .prettyPrinting(true)
                        .defaultOptions(opts ->
                                opts.serializers(serializers))
                        .headerMode(HeaderMode.PRESERVE);

        if (builder.loaderCustomizer != null) {
            builder.loaderCustomizer.accept(loaderBuilder);
        }

        this.loader = loaderBuilder.build();

        FileManagerService.getInstance().addHandler(builder.plugin, this);

        instantEnable(builder.instantEnable);
    }

    /**
     * Logic executed when the handler is loaded.
     */
    @Override
    public void handlerLoadLogic() throws ConfigurateException {
        // Initial load only. readInternal will handle later loads.
        configurationNode = loader.load();
    }

    @Override
    public ReloadResult handlerReloadLogic(boolean resourceFound) {
        try {
            ConfigurationNode snapshotNode = loader.createNode();
            snapshotNode.set(classType, configurationObject);
            T backupConfiguration = snapshotNode.get(classType);

            try {
                if (!resourceFound) {
                    throw new FileNotFoundException("Configuration file not found at " + pluginFolderPath);
                }

                // Explicitly catch empty/virtual files during reload
                if (configurationNode.virtual()) {
                    throw new IOException("Loaded node is virtual (file might be empty)");
                }

                // We tell read logic NOT to handle corruption itself
                // If readInternal() fails, we will end up in the catch case
                readInternal(false);
                return ReloadResult.READ_NEW_FILE;

            } catch (Exception e) {
                getLogger(plugin).error("[Handler] (Error) - '{}' reloading configuration file! Reverting to last known good state.", path, e);

                try {
                    handleCorruption(backupConfiguration);

                    return ReloadResult.REVERTED_TO_BACKUP;
                } catch (Exception restoreEx) {
                    getLogger(plugin).error("[Handler] (Error) - '{}' Failed to restore backup during reload!", path, restoreEx);
                    return ReloadResult.ERROR_READING_FILE;
                }
            }
        } catch (Exception e) {
            getLogger(plugin).error("[Handler] (Error) - '{}' Cannot create backup for reload!", path,e);
            return ReloadResult.CANNOT_MAKE_BACKUP;
        }
    }

    @Override
    public void destroyHandler() {
        getLogger(plugin).debug("[Handler] (Destroy) - '{}'",  path);
        configurationObject = null;
    }

    /**
     * Loads the configuration file into memory.
     *
     * @throws ConfigurateException if loading the configuration fails
     */
    @Override
    public void handlerEnableLogic() throws Exception {
        read();
    }

    /**
     * Reads the configuration data from the {@link ConfigurationNode}
     * and maps it to the configuration object.
     *
     * @return {@code true} if reading was successful
     * @throws ConfigurateException if deserialization fails
     */
    @Override
    public boolean handlerReadLogic() throws Exception {
        // Normal reads (like enable/startup) ARE allowed to auto-recover to defaults
        readInternal(true);
        return true;
    }

    /**
     * Writes the current configuration object back to the YAML file.
     *
     * @throws ConfigurateException if serialization or saving fails
     */
    @Override
    public void handlerWriteLogic() throws ConfigurateException {
        saveInternal();
    }

    /**
     * Logic executed when the handler is disabled.
     *
     * <p>No default behavior.</p>
     */
    @Override
    public void handlerDisableLogic() throws ConfigurateException {
        handlerWriteLogic();
    }

    /**
     * Internal read logic with a toggle for automatic corruption recovery.
     * @param allowRecovery if true, corrupted files are moved to .old and replaced with defaults.
     *                      if false, corruption throws an exception.
     */
    private void readInternal(boolean allowRecovery) throws Exception {
        getLogger(plugin).debug("[Handler] (Reading) - '{}'", path);

        if (Files.notExists(pluginFolderPath)) {
            getLogger(plugin).debug("[Handler] (Reading) - '{}' Creating new folder at {}", path, pluginFolderPath);
            // Case 1: Brand new file
            configurationObject = createDefaultInstance();
            saveInternal();
            return; // We have our object, stop here.
        }

        // Always load fresh from disk to avoid "Ghost Nodes"
        configurationNode = loader.load();

        try {
            // Case 2: Existing file, try to map it

            // Handle empty files
            if (configurationNode.virtual()) {
                getLogger(plugin).debug("[Handler] (Reading) - '{}' Loaded node is virtual (file might be empty)", path);
                throw new IOException("Configuration node is virtual");
            }

            T loaded = configurationNode.get(classType);
            if (loaded == null){
                getLogger(plugin).debug("[Handler] (Reading) - '{}' Loaded node is null", path);
                throw new IOException("Configuration resulted in null object");
            }

            configurationObject = loaded;
            getLogger(plugin).debug("[Handler] (Reading) (SUCCESS) - '{}'", path);

            // SUCCESS: Update the timestamp so the task knows we are up to date
            updateLocallyLastModified();
        } catch (Exception e) {
            // Case 3: Corrupted file
            if (allowRecovery) {
                getLogger(plugin).warn("[Handler] (Recovery) - '{}' is corrupted! Restoring defaults.", path);
                // configurationObject is set inside handleCorruption, so we are good.
                handleCorruption(classType.getDeclaredConstructor().newInstance());
            }
            // If recovery isn't allowed (like during reload), throw so the caller handles it
            throw e;
        }
    }

    /**
     * Handles file corruption by moving the broken file to a .old backup
     * and restoring the file from the provided fallback object.
     */
    private void handleCorruption(T fallback) throws Exception {
        if (Files.exists(pluginFolderPath)) {
            Path oldPath = Handler.moveToOld(pluginFolderPath);
            getLogger(plugin).warn("[Handler] (Recovery) - '{}' Moved broken configuration file to '{}'", path, oldPath);
        }

        this.configurationObject = fallback;
        saveInternal();
        getLogger(plugin).info("[Handler] (Recovery) - '{}' Restored configuration file from memory/defaults", path);
    }

    /**
     * Shared logic to sync the in-memory object to the node and then to disk.
     */
    private void saveInternal() throws ConfigurateException {
        getLogger(plugin).debug("[Handler] (Writing) - '{}'", path);

        configurationNode = loader.createNode();
        configurationNode.set(classType, configurationObject);
        loader.save(configurationNode);

        getLogger(plugin).debug("[Handler] (Writing) (SUCCES) - '{}'", path);

        // SUCCESS: Update the timestamp so the task knows we are up to date
        updateLocallyLastModified();
    }

    /**
     * Centralized instantiation with better error reporting
     */
    private T createDefaultInstance() throws Exception {
        try {
            getLogger(plugin).debug("[Handler] (Reading) - '{}' Creating new instance of {}", path, classType.getSimpleName());
            return classType.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Config class " + classType.getName() + " is missing a public no-args constructor!", e);
        }
    }

    /**
     * Returns the current configuration object.
     *
     * @return the configuration object, or {@code null} if the handler
     * has not yet been initialized
     */
    public T getConfigurationObject() {
        // We allow access even if the handler is disabled, as long as we actually
        // have a configuration object. This is important during plugin shutdown,
        // where loggers may still need to read their config while handlers are
        // being torn down.
        if (configurationObject == null) {
            throw new IllegalStateException("Configuration object has not been initialised yet");
        }
        return configurationObject;
    }

    public static final class Builder<T> {

        private final FoundationDefaultPlugin<?> plugin;
        private @Nullable FoundationLogger logger;
        private final Class<T> classType;
        private final String path;

        private boolean instantEnable = false;

        private int priority = 0;

        private @Nullable Consumer<ReloadResult> afterReloadConsumer;
        private @Nullable Runnable preLoadLogic;
        private @Nullable Runnable postLoadLogic;
        private @Nullable Runnable preEnableLogic;
        private @Nullable Runnable postEnableLogic;
        private @Nullable Runnable preDisableLogic;
        private @Nullable Runnable postDisableLogic;

        private final Set<SerializerBundle> bundles = new HashSet<>();
        private Consumer<HoconConfigurationLoader.Builder> loaderCustomizer;

        private Builder(FoundationDefaultPlugin<?> plugin,
                        Class<T> classType,
                        String path) {
            this.plugin = plugin;
            this.classType = classType;
            this.path = path;
        }

        public Builder<T> instantEnable(boolean value) {
            this.instantEnable = value;
            return this;
        }

        public Builder<T> priority(int value) {
            this.priority = value;
            return this;
        }

        public Builder<T> logger(@Nullable FoundationLogger logger) {
            this.logger = logger;
            return this;
        }

        public Builder<T> setAfterReloadConsumer(Consumer<ReloadResult> afterReloadConsumer) {
            this.afterReloadConsumer = afterReloadConsumer;
            return this;
        }

        public Builder<T> setPreLoadLogic(Runnable preLoadLogic) {
            this.preLoadLogic = preLoadLogic;
            return this;
        }

        public Builder<T> setPreEnableLogic(Runnable preEnableLogic) {
            this.preEnableLogic = preEnableLogic;
            return this;
        }

        public Builder<T> setPreDisableLogic(Runnable preDisableLogic) {
            this.preDisableLogic = preDisableLogic;
            return this;
        }

        public Builder<T> setPostLoadLogic(Runnable postLoadLogic) {
            this.postLoadLogic = postLoadLogic;
            return this;
        }

        public Builder<T> setPostEnableLogic(Runnable postEnableLogic) {
            this.postEnableLogic = postEnableLogic;
            return this;
        }

        public  Builder<T> setPostDisableLogic(Runnable postDisableLogic) {
            this.postDisableLogic = postDisableLogic;
            return this;
        }

        public Builder<T> addBundle(SerializerBundle bundle) {
            this.bundles.add(bundle);
            return this;
        }

        public Builder<T> addBundles(SerializerBundle... bundles) {
            Collections.addAll(this.bundles, bundles);
            return this;
        }

        public Builder<T> customizeLoader(
                Consumer<HoconConfigurationLoader.Builder> customizer
        ) {
            this.loaderCustomizer = customizer;
            return this;
        }

        public ConfigurationHandler<T> build() {
            if (bundles.isEmpty()) {
                bundles.add(SerializerBundles.all(plugin));
            }
            return new ConfigurationHandler<>(this);
        }
    }
}
