package net.astralounge.foundationapi.common.datamanagement.files.handler.implementation;

import net.astralounge.foundationapi.common.datamanagement.files.FileManagerService;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.Handler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.ReloadableHandler;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.models.ReloadResult;
import net.astralounge.foundationapi.common.logger.loggers.FoundationLogger;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A handler responsible for managing a folder containing multiple files
 * of the same type, each handled by a {@link RegisteredFileHandler}.
 *
 * <p>This handler can optionally extract default resources from the plugin JAR
 * into the target folder if it is empty.</p>
 *
 * @param <T> the type of object stored in each file
 */
public class FolderHandler<T> extends Handler implements ReloadableHandler {

    private final Type type;

    /**
     * List of file handlers representing each file in the folder.
     */
    private final List<RegisteredFileHandler<T>> fileHandlersList;

    /**
     * Whether default resources should be copied from the JAR
     * when the folder is empty.
     */
    private final boolean defaultResource;

    private final FoundationDefaultPlugin<?> plugin;

    /**
     * Constructs a new {@link FolderHandler}.
     *
     * @param plugin          the owning plugin instance
     * @param path            the relative folder path
     * @param instantEnable   whether the configuration should be loaded and enabled instantly
     * @param defaultResource whether default resources should be extracted
     */
    public FolderHandler(
            FoundationDefaultPlugin<?> plugin,
            Type classType,
            String path,
            boolean instantEnable,
            boolean defaultResource) {
        super(plugin, path, instantEnable);
        this.plugin = plugin;
        this.type = classType;
        this.defaultResource = defaultResource;
        this.fileHandlersList = new ArrayList<>();
        FileManagerService.getInstance().addHandler(plugin, this);
        instantEnable(instantEnable);
    }

    /**
     * Initializes the folder handler.
     *
     * <p>This method ensures the folder exists, optionally extracts default
     * resources from the JAR, and creates a {@link SimpleFileHandler} for each file.</p>
     *
     * @throws Exception if the path is not a directory
     */
    @Override
    public void handlerLoadLogic() throws Exception {
        getFile().mkdirs();
        if (!getFile().isDirectory()) throw new Exception("Path is not a directory!");

        if (isFolderEmpty() && defaultResource) {
            try {
                extractFolderFromJar(getLogger(plugin), path, getPluginFolderPath());
            } catch (Exception exception) {
                getLogger(plugin).error(exception);
            }
        }

        for (File file : getFolderFiles()) {
            getLogger(plugin).debug("Loading file: {}", file.getName());
            if (isFileBackupOrOld(file)){
                getLogger(plugin).warn("Found backup or old file: {}", file.getName());
                continue;
            }

            // Build a per-file path relative to the plugin's data folder
            String fileRelativePath = path + File.separator + file.getName();

            RegisteredFileHandler<T> fileHandler = new RegisteredFileHandler<>(
                    RegisteredFileHandler.builder(plugin, type, fileRelativePath), plugin);
            fileHandler.load();
            fileHandlersList.add(fileHandler);
        }
    }

    /**
     * Logic executed when the handler is enabled.
     *
     * <p>No default behavior.</p>
     */
    @Override
    public void handlerEnableLogic() {
        // No-op
    }

    /**
     * Logic executed when the handler is disabled.
     *
     * <p>Clears all registered file handlers.</p>
     */
    @Override
    public void handlerDisableLogic() {
        fileHandlersList.clear();
    }

    /**
     * Returns all file handlers managed by this folder handler.
     *
     * @return a list of {@link RegisteredFileHandler} instances
     */
    public List<RegisteredFileHandler<T>> getFileHandlersList() {
        return fileHandlersList;
    }

    /**
     * Returns all files inside the folder.
     *
     * @return an array of files, or an empty array if none exist
     */
    public File[] getFolderFiles() {
        File[] files = getFile().listFiles();
        return files != null ? files : new File[0];
    }

    /**
     * Checks whether the folder is empty.
     *
     * @return {@code true} if the folder has no files or cannot be read
     */
    public boolean isFolderEmpty() {
        File[] files = getFile().listFiles();
        return files == null || files.length == 0;
    }

    private boolean isFileBackupOrOld(File file) {
        return file.getName().endsWith(".bak") || file.getName().endsWith(".old");
    }

    /**
     * Extracts a folder from the plugin JAR into the given output folder.
     *
     * <p>Only files that do not already exist are copied.</p>
     *
     * @param resourceFolder the folder path inside the JAR
     * @param outputFolder   the destination folder on disk
     * @throws IOException        if an I/O error occurs
     * @throws URISyntaxException if the JAR location cannot be resolved
     */
    public static void extractFolderFromJar(
            FoundationLogger logger,
            String resourceFolder,
            Path outputFolder
    ) throws IOException, URISyntaxException {
        logger.debug("Extracting default resources from JAR from {} to {}", resourceFolder, outputFolder);

        String jarPath = FolderHandler.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();

        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.startsWith(resourceFolder + "/") && !entry.isDirectory()) {
                    logger.debug("Found file {}", entryName);
                    File outputFile = new File(
                            outputFolder.toString(),
                            entryName.substring(resourceFolder.length() + 1)
                    );

                    outputFile.getParentFile().mkdirs();

                    if (!outputFile.exists()) {
                        logger.debug("Copying file {}", outputFile.getName());
                        try (InputStream is = jarFile.getInputStream(entry);
                             OutputStream os = new FileOutputStream(outputFile)) {

                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }
                        }
                    } else {
                        logger.debug("File {} already exists, skipping", outputFile.getName());
                    }
                }
            }
        }
    }

    @Override
    public ReloadResult reload() {
        ReloadResult result = ReloadResult.FOLDER_RELOADED;

        for (SimpleFileHandler<T> fileHandler : fileHandlersList) {
            ReloadResult fileResult = fileHandler.reload();
            switch (fileResult) {
                case UNKNOWN,
                     NOT_INITIALISED,
                     ERROR_READING_FILE -> result = ReloadResult.NOT_ALL_FOLDER_FILES_RELOADED;
            }
        }
        return result;
    }
}