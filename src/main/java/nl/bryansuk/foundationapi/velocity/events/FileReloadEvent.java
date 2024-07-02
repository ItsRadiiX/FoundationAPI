package nl.bryansuk.foundationapi.velocity.events;

import com.velocitypowered.api.event.Event;

public class FileReloadEvent implements Event {
    private final String fileName;

    public FileReloadEvent(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

}
