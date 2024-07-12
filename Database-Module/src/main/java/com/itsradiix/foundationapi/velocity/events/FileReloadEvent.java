package com.itsradiix.foundationapi.velocity.events;


public class FileReloadEvent {
    private final String fileName;

    public FileReloadEvent(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

}
