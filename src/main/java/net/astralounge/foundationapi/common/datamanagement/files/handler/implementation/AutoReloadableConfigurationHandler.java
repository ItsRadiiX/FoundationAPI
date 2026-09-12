package net.astralounge.foundationapi.common.datamanagement.files.handler.implementation;

import net.astralounge.foundationapi.common.datamanagement.files.configuration.AutoReloadableConfiguration;
import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.AutoReloadableHandler;

public class AutoReloadableConfigurationHandler<T extends AutoReloadableConfiguration>
        extends ConfigurationHandler<T>
        implements AutoReloadableHandler {

    private boolean isAutoReloading;

    public AutoReloadableConfigurationHandler(Builder<T> builder, boolean isAutoReloading) {
        super(builder);
        this.isAutoReloading = isAutoReloading;
    }

    @Override
    public boolean handlerReadLogic() throws Exception {
        boolean read = super.handlerReadLogic();

        isAutoReloading = getConfigurationObject().autoReloadConfiguration;

        return read;
    }

    @Override
    public boolean isAutoReloadEnabled() {
        return isAutoReloading;
    }

    @Override
    public void setAutoReloadEnabled(boolean enabled) {
        this.isAutoReloading = enabled;
        getConfigurationObject().autoReloadConfiguration = enabled;
    }
}
