package com.itsradiix.foundationapi.common.datamanagement.files.handlers;

import com.itsradiix.foundationapi.common.datamanagement.files.converter.Converter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                        defaultResource,
                        isAutoReloading());
                fileHandlersList.add(fileHandler);
                fileHandler.read();
            }
        }

        updateLastModified();
    }

    @Override
    public boolean onReload() {
        return false;
    }

    @Override
    public void destroy() {
        clearFileHandlers();
    }

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

}
