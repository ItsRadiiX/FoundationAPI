package net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction;

import net.astralounge.foundationapi.common.datamanagement.files.handler.abstraction.models.ReloadResult;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Consumer;

public abstract class ReadWriteHandler extends Handler implements ReloadableHandler {
    private @Nullable Consumer<ReloadResult> afterReloadConsumer;


    public ReadWriteHandler(File pluginFolderPath, String path, boolean instantEnable) {
        super(pluginFolderPath, path, instantEnable);
    }

    public ReadWriteHandler(File pluginFolderPath, String path, boolean instantEnable,
                            @Nullable Consumer<ReloadResult> afterReloadConsumer) {
        super(pluginFolderPath, path, instantEnable);
        this.afterReloadConsumer = afterReloadConsumer;
    }

    public ReadWriteHandler(FoundationDefaultPlugin<?> plugin, String path, boolean instantEnable) {
        super(plugin, path, instantEnable);
    }

    public ReadWriteHandler(FoundationDefaultPlugin<?> plugin, String path, boolean instantEnable,
                            @Nullable Consumer<ReloadResult> afterReloadConsumer) {
        super(plugin, path, instantEnable);
        this.afterReloadConsumer = afterReloadConsumer;
    }

    protected abstract boolean handlerReadLogic() throws Exception;

    protected abstract void handlerWriteLogic() throws Exception;

    protected abstract ReloadResult handlerReloadLogic(boolean isResourceFound);

    public abstract void destroyHandler();

    public void read() {
        if (returnIfNotEnabled()) return;

        try {
            handlerReadLogic();
        } catch (Exception exception) {
            getLogger(plugin).error("[Handler] (Error) - '{}' could not be read!", path, exception);
        }
    }

    public void write() {
        if (returnIfNotEnabled()) return;
        try {
            handlerWriteLogic();
        } catch (Exception exception) {
            getLogger(plugin).error("[Handler] (Error) - '{}' could not be written to!", path, exception);
        }
    }

    @Override
    public ReloadResult reload() {
        if (returnIfNotEnabled()) return ReloadResult.NOT_INITIALISED;

        ReloadResult result;

        if (isNewVersionAvailable()) {
            result = handlerReloadLogic(isResourceFound());
        } else {
            result = ReloadResult.NO_UPDATE;
        }

        if (afterReloadConsumer != null) afterReloadConsumer.accept(result);

        return result;
    }

    public void setAfterReloadConsumer(@Nullable Consumer<ReloadResult> afterReloadConsumer) {
        this.afterReloadConsumer = afterReloadConsumer;
    }


}
