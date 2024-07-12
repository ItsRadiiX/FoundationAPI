package com.itsradiix.foundationapi.paper.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FolderReloadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String folderName;

    public FolderReloadEvent(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
