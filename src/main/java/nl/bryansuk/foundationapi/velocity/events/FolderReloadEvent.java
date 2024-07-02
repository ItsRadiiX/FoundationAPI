package nl.bryansuk.foundationapi.velocity.events;

import com.velocitypowered.api.event.Event;

public class FolderReloadEvent implements Event {
    private final String folderName;

    public FolderReloadEvent(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }

}
