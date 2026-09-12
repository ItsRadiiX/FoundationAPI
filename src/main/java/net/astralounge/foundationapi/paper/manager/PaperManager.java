package net.astralounge.foundationapi.paper.manager;

import net.astralounge.foundationapi.common.manager.CommonManager;
import net.astralounge.foundationapi.paper.plugin.FoundationPaperPlugin;

public abstract class PaperManager<T extends FoundationPaperPlugin<T, ?>>
        extends CommonManager {

    private final T foundationPaperPlugin;

    public PaperManager(T foundationPaperPlugin) {
        super(foundationPaperPlugin);
        this.foundationPaperPlugin = foundationPaperPlugin;
    }

    @Override
    public T getPlugin() {
        return foundationPaperPlugin;
    }
}
