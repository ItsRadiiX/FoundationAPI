package com.itsradiix.foundationapi.velocity.events;


public class FolderReloadEvent {
    private final String folderName;

    public FolderReloadEvent(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }

}
