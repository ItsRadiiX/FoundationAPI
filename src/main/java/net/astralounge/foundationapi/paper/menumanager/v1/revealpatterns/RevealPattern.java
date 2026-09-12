package net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns;

import java.util.Set;

public interface RevealPattern {

    /**
     * Generates the reveal order for a rectangular area using default options.
     *
     * @param rows number of rows in the area
     * @param cols number of columns in the area
     * @return slot indices (0 ... rows*cols-1)
     */
    default int[] generate(int rows, int cols) {
        return generate(rows, cols, PatternOptions.empty());
    }

    /**
     * Generates the reveal order for a rectangular area using pattern-specific options.
     *
     * @param rows    number of rows in the area
     * @param cols    number of columns in the area
     * @param options pattern-specific options
     * @return slot indices (0 ... rows*cols-1)
     */
    int[] generate(int rows, int cols, PatternOptions options);

    /**
     * @return the set of supported option keys (for tooling / UI).
     * Implementations may return an empty set if they don't use options.
     */
    default Set<PatternOptionKey<?>> supportedOptions() {
        return Set.of();
    }
}
