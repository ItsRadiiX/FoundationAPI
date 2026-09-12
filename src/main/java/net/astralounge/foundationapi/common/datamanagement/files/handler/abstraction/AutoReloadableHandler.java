package net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction;

public interface AutoReloadableHandler extends ReloadableHandler {
    boolean isAutoReloadEnabled();
    void setAutoReloadEnabled(boolean enabled);
}
