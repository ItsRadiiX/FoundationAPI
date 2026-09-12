package net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.implementations;

import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.PatternOptionKey;
import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.PatternOptions;
import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.RevealPattern;

import java.util.Set;

public class ZigZagPattern implements RevealPattern {

    /**
     * How the zigzag traverses the grid.
     */
    public enum Orientation {
        ROW_WISE,      // default: row by row
        COLUMN_WISE    // column by column
    }

    /**
     * Overall vertical direction of traversal.
     */
    public enum VerticalDirection {
        TOP_DOWN,      // default
        BOTTOM_UP
    }

    /**
     * Which horizontal direction is used on the first "line"
     * (row in ROW_WISE, column in COLUMN_WISE)
     */
    public enum HorizontalDirection {
        LEFT_TO_RIGHT_FIRST,  // default
        RIGHT_TO_LEFT_FIRST
    }

    public static final PatternOptionKey<Orientation> ORIENTATION =
            PatternOptionKey.of("zigzag.orientation");

    public static final PatternOptionKey<VerticalDirection> VERTICAL_DIRECTION =
            PatternOptionKey.of("zigzag.verticalDirection");

    public static final PatternOptionKey<HorizontalDirection> HORIZONTAL_DIRECTION =
            PatternOptionKey.of("zigzag.horizontalDirection");

    @Override
    public Set<PatternOptionKey<?>> supportedOptions() {
        return Set.of(ORIENTATION, VERTICAL_DIRECTION, HORIZONTAL_DIRECTION);
    }

    @Override
    public int[] generate(int rows, int cols, PatternOptions options) {
        Orientation orientation = options.get(ORIENTATION, Orientation.ROW_WISE);
        VerticalDirection verticalDirection = options.get(VERTICAL_DIRECTION, VerticalDirection.TOP_DOWN);
        HorizontalDirection horizontalDirection = options.get(HORIZONTAL_DIRECTION, HorizontalDirection.LEFT_TO_RIGHT_FIRST);

        return switch (orientation) {
            case ROW_WISE -> generateRowWise(rows, cols, verticalDirection, horizontalDirection);
            case COLUMN_WISE -> generateColumnWise(rows, cols, verticalDirection, horizontalDirection);
        };
    }

    /**
     * Classic zigzag: row by row, alternating left→right / right→left.
     */
    private int[] generateRowWise(int rows,
                                  int cols,
                                  VerticalDirection verticalDirection,
                                  HorizontalDirection horizontalDirection) {

        int[] order = new int[rows * cols];
        int index = 0;

        for (int rIndex = 0; rIndex < rows; rIndex++) {
            int row = (verticalDirection == VerticalDirection.TOP_DOWN)
                    ? rIndex
                    : rows - 1 - rIndex;

            // Decide direction for this row:
            boolean leftToRight;
            if (horizontalDirection == HorizontalDirection.LEFT_TO_RIGHT_FIRST) {
                leftToRight = (rIndex % 2 == 0);
            } else {
                leftToRight = (rIndex % 2 != 0);
            }

            if (leftToRight) {
                for (int col = 0; col < cols; col++) {
                    order[index++] = row * cols + col;
                }
            } else {
                for (int col = cols - 1; col >= 0; col--) {
                    order[index++] = row * cols + col;
                }
            }
        }

        return order;
    }

    /**
     * Column-wise zigzag: column by column, alternating top→bottom / bottom→top.
     */
    private int[] generateColumnWise(int rows,
                                     int cols,
                                     VerticalDirection verticalDirection,
                                     HorizontalDirection horizontalDirection) {

        int[] order = new int[rows * cols];
        int index = 0;

        // cIndex is the "logical" column index in the zigzag sequence.
        for (int cIndex = 0; cIndex < cols; cIndex++) {

            // Map logical index to actual column, depending on horizontal direction.
            int col = (horizontalDirection == HorizontalDirection.LEFT_TO_RIGHT_FIRST)
                    ? cIndex
                    : (cols - 1 - cIndex);

            // Decide vertical direction for this column based on sequence index.
            boolean topToBottom;
            if (verticalDirection == VerticalDirection.TOP_DOWN) {
                topToBottom = (cIndex % 2 == 0);
            } else {
                topToBottom = (cIndex % 2 != 0);
            }

            if (topToBottom) {
                for (int row = 0; row < rows; row++) {
                    order[index++] = row * cols + col;
                }
            } else {
                for (int row = rows - 1; row >= 0; row--) {
                    order[index++] = row * cols + col;
                }
            }
        }

        return order;
    }
}