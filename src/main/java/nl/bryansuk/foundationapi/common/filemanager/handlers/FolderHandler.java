package nl.bryansuk.foundationapi.common.filemanager.handlers;

import nl.bryansuk.foundationapi.common.filemanager.FileManager;
import nl.bryansuk.foundationapi.common.filemanager.converter.Converter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class FolderHandler<T> extends Handler {

    private final Converter<T> converter;
    private final List<FileHandler<T>> fileHandlersList;

    public FolderHandler(String path, Converter<T> converter, boolean isAutoReloading) {
        super(path, isAutoReloading);
        this.converter = converter;
        this.fileHandlersList = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void initialize(){
        File[] files = getFolderFiles();

        clearFileHandlers();

        if (!isFolderEmpty()){
            for (File file : files){
                FileHandler<T> fileHandler = new FileHandler<>(
                        getPath()
                                + getFolderSeparator()
                                + file.getName(),
                        converter,
                        false,
                        false);
                fileHandlersList.add(fileHandler);
                fileHandler.read();
            }
        }

        updateLastModified();
    }

    @Override
    public boolean onReload() {
        if (isNewVersionAvailable()){
            initialize();
        } else {
            fileHandlersList.forEach(FileHandler::onReload);
        }
        FileManager.getInstance().callFolderReloadEvent(getFile().getName());
        return true;
    }

    @Override
    public void destroy() {
        clearFileHandlers();
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

}
