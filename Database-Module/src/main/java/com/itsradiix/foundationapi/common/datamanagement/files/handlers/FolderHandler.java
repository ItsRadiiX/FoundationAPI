package com.itsradiix.foundationapi.common.datamanagement.files.handlers;

import com.itsradiix.foundationapi.common.datamanagement.files.FileManager;
import com.itsradiix.foundationapi.common.datamanagement.files.converter.Converter;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SuppressWarnings("unused")
public class FolderHandler<T> extends Handler {

    private final Converter<T> converter;
    private final List<FileHandler<T>> fileHandlersList;
    private final boolean defaultResource;

    public FolderHandler(String path, Converter<T> converter, boolean defaultResource, boolean isAutoReloading) {
        super(path, isAutoReloading);
        this.converter = converter;
        this.defaultResource = defaultResource;
        this.fileHandlersList = Collections.synchronizedList(new ArrayList<>());
        initialize();
    }

    @Override
    public void initialize() {
        if (isFolderEmpty() && defaultResource) {
            // Copy the files that do not yet exist in the plugin folder
            // the ones that do exist should be updated by the FileHandlers
            try {
                extractFolderFromJar(getPath(), getFilePath());
            } catch (Exception e) {
                FileManager.getLogger().info(e.getMessage());
            }
        }

        File[] files = getFolderFiles();

        clearFileHandlers();

        for (File file : files){

            FileHandler<T> fileHandler = new FileHandler<>(
                    getPath()
                            + getFolderSeparator()
                            + file.getName(),
                    converter,
                    defaultResource,
                    isAutoReloading());
            fileHandlersList.add(fileHandler);
            fileHandler.read();
        }


        updateLastModified();
    }

    @Override
    public boolean onReload() {
        return false;
    }

    @Override
    public void destroy() {}

    public List<FileHandler<T>> getFileHandlersList() {
        return fileHandlersList;
    }

    public List<T> getObjects(){
        return fileHandlersList.stream()
                .map(FileHandler::getObject)
                .toList();
    }

    private void clearFileHandlers(){
        fileHandlersList.forEach(FileHandler::destroy);
        fileHandlersList.clear();
    }

    public File[] getFolderFiles(){
        File[] files = getFile().listFiles();
        return files != null ? files : new File[]{};
    }

    public boolean isFolderEmpty(){
        File[] files = getFile().listFiles();
        if (files == null) return true;
        return files.length == 0;
    }

    public static void extractFolderFromJar(String resourceFolder, String outputFolder) throws IOException, URISyntaxException {
        // Locate the JAR file containing this class
        String jarPath = FolderHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

        // Open JAR file
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                // Check if this entry belongs to the resource folder
                if (entryName.startsWith(resourceFolder + "/") && !entry.isDirectory()) {
                    // Compute the destination file path
                    File outputFile = new File(outputFolder, entryName.substring(resourceFolder.length() + 1));
                    outputFile.getParentFile().mkdirs();  // Create necessary directories

                    if (!outputFile.exists()) {
                        // Copy content
                        try (InputStream is = jarFile.getInputStream(entry);
                             OutputStream os = new FileOutputStream(outputFile)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                }
            }
        }
    }

}
