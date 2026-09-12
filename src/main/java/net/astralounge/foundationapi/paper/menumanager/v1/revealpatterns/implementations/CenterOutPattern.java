package net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.implementations;

import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.PatternOptionKey;
import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.PatternOptions;
import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.RevealPattern;

import java.util.Set;

public class CenterOutPattern implements RevealPattern {

    public static final PatternOptionKey<Direction> DIRECTION =
            PatternOptionKey.of("centerOut.direction");

    public static final PatternOptionKey<Spin> SPIN =
            PatternOptionKey.of("centerOut.spin");

    @Override
    public Set<PatternOptionKey<?>> supportedOptions() {
        return Set.of(DIRECTION, SPIN);
    }

    @Override
    public int[] generate(int rows, int cols) {
        // Default: OUTER_TO_CENTER + CLOCKWISE to match legacy behavior
        return generate(rows, cols, PatternOptions.empty());
    }

    @Override
    public int[] generate(int rows, int cols, PatternOptions options) {
        Direction direction = options.get(DIRECTION, Direction.OUTER_TO_CENTER);
        Spin spin = options.get(SPIN, Spin.CLOCKWISE);

        int[] baseOrder = (spin == Spin.CLOCKWISE)
                ? generateOuterToCenterClockwise(rows, cols)
                : generateOuterToCenterCounterClockwise(rows, cols);

        if (direction == Direction.OUTER_TO_CENTER) {
            return baseOrder;
        }

        // CENTER_TO_OUTER -> reverse the order
        int[] reversed = new int[baseOrder.length];
        for (int i = 0; i < baseOrder.length; i++) {
            reversed[i] = baseOrder[baseOrder.length - 1 - i];
        }
        return reversed;
    }

    /**
     * Existing spiral implementation: outer border to center, clockwise.
     */
    private int[] generateOuterToCenterClockwise(int rows, int cols) {
        int[] order = new int[rows * cols];
        int index = 0;

        int minRow = 0, maxRow = rows - 1;
        int minCol = 0, maxCol = cols - 1;

        while (minRow <= maxRow && minCol <= maxCol) {

            // top row
            for (int col = minCol; col <= maxCol; col++) {
                order[index++] = minRow * cols + col;
            }
            minRow++;

            // right column
            for (int row = minRow; row <= maxRow; row++) {
                order[index++] = row * cols + maxCol;
            }
            maxCol--;

            if (minRow <= maxRow) {
                // bottom row
                for (int col = maxCol; col >= minCol; col--) {
                    order[index++] = maxRow * cols + col;
                }
                maxRow--;
            }

            if (minCol <= maxCol) {
                // left column
                for (int row = maxRow; row >= minRow; row--) {
                    order[index++] = row * cols + minCol;
                }
                minCol++;
            }
        }

        return order;
    }

    /**
     * Outer border to center, counter‑clockwise.
     * (One simple approach is to mirror or rotate indices from the clockwise version.
     * Here it's written explicitly for clarity.)
     */
    private int[] generateOuterToCenterCounterClockwise(int rows, int cols) {
        int[] order = new int[rows * cols];
        int index = 0;

        int minRow = 0, maxRow = rows - 1;
        int minCol = 0, maxCol = cols - 1;

        while (minRow <= maxRow && minCol <= maxCol) {

            // left column (top -> bottom)
            for (int row = minRow; row <= maxRow; row++) {
                order[index++] = row * cols + minCol;
            }
            minCol++;

            // bottom row (left -> right)
            for (int col = minCol; col <= maxCol; col++) {
                order[index++] = maxRow * cols + col;
            }
            maxRow--;

            if (minCol <= maxCol) {
                // right column (bottom -> top)
                for (int row = maxRow; row >= minRow; row--) {
                    order[index++] = row * cols + maxCol;
                }
                maxCol--;
            }

            if (minRow <= maxRow) {
                // top row (right -> left)
                for (int col = maxCol; col >= minCol; col--) {
                    order[index++] = minRow * cols + col;
                }
                minRow++;
            }
        }

        return order;
    }

    /**
     * Option: in which direction the spiral is revealed (from where to where).
     */
    public enum Direction {
        OUTER_TO_CENTER,
        CENTER_TO_OUTER
    }

    /**
     * Option: spin of the spiral (right/left).
     */
    public enum Spin {
        CLOCKWISE,
        COUNTER_CLOCKWISE
    }
}
