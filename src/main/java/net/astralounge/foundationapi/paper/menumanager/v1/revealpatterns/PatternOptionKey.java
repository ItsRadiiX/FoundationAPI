package net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns;

import org.jetbrains.annotations.NotNull;

/**
 * Typed key used for options in {@link PatternOptions}.
 */
public final class PatternOptionKey<T> {

    private final String name;

    private PatternOptionKey(String name) {
        this.name = name;
    }

    public static <T> PatternOptionKey<T> of(@NotNull String name) {
        return new PatternOptionKey<>(name);
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return "PatternOptionKey[" + name + ']';
    }
}