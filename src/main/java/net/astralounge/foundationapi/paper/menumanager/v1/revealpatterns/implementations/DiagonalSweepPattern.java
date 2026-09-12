package net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.implementations;

import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.PatternOptionKey;
import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.PatternOptions;
import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.RevealPattern;

import java.util.Set;

public class DiagonalSweepPattern implements RevealPattern {
    /**
     * Option: where the diagonal starts.
     */
    public enum StartingPoint {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public static final PatternOptionKey<StartingPoint> STARTING_POINT =
            PatternOptionKey.of("diagonal.startingPoint");

    @Override
    public int[] generate(int rows, int columns, PatternOptions options) {
        int totalSlots = rows * columns;
        int[] order = new int[totalSlots];
        int index = 0;

        StartingPoint startingPoint = options.get(STARTING_POINT, StartingPoint.TOP_LEFT);

        for (int sum = 0; sum < rows + columns - 1; sum++) {
            for (int i = 0; i <= sum; i++) {
                int row, col;
                col = switch (startingPoint) {
                    case TOP_LEFT -> {
                        row = i;
                        yield sum - i;
                    }
                    case TOP_RIGHT -> {
                        row = i;
                        yield columns - 1 - (sum - i);
                    }
                    case BOTTOM_LEFT -> {
                        row = rows - 1 - i;
                        yield sum - i;
                    }
                    case BOTTOM_RIGHT -> {
                        row = rows - 1 - i;
                        yield columns - 1 - (sum - i);
                    }
                };

                if (row >= 0 && row < rows && col >= 0 && col < columns) {
                    order[index++] = row * columns + col;
                }
            }
        }

        return order;
    }

    @Override
    public Set<PatternOptionKey<?>> supportedOptions() {
        return Set.of(STARTING_POINT);
    }
}
