package net.astralounge.foundationapi.common.dependencies;

import net.astralounge.foundationapi.common.startup.LoadError;

public class PluginDependency {
    protected final String name;
    protected final Level level;
    protected boolean loaded;

    public PluginDependency(String name, Level level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public Level getLevel() {
        return level;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public enum Level {
        SOFT_PLUGIN(LoadError.Level.RISK),
        CRITICAL_PLUGIN(LoadError.Level.CRITICAL);

        private final LoadError.Level level;

        Level(LoadError.Level level) {
            this.level = level;
        }

        public LoadError.Level getLoadErrorLevel() {
            return level;
        }
    }
}
