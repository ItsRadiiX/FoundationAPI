package net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Type-safe container for pattern options.
 */
public final class PatternOptions {

    private final Map<PatternOptionKey<?>, Object> values;

    private PatternOptions(Map<PatternOptionKey<?>, Object> values) {
        this.values = values;
    }

    public static PatternOptions empty() {
        return new PatternOptions(Collections.emptyMap());
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(@NotNull PatternOptionKey<T> key, T defaultValue) {
        Object value = values.get(key);
        if (value == null) {
            return defaultValue;
        }
        return (T) value; // safe if everyone uses the key's declared type
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatternOptions that)) return false;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    public static final class Builder {

        private final Map<PatternOptionKey<?>, Object> values = new HashMap<>();

        public <T> Builder set(@NotNull PatternOptionKey<T> key, T value) {
            values.put(key, value);
            return this;
        }

        public PatternOptions build() {
            return new PatternOptions(Map.copyOf(values));
        }
    }
}
