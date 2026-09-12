package net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.implementations;

import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.PatternOptionKey;
import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.PatternOptions;
import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.RevealPattern;

import java.util.Set;

public class RowByRowPattern implements RevealPattern {

    /**
     * Which direction we move through the rows.
     */
    public enum VerticalDirection {
        TOP_DOWN,   // default: row 0 -> last row
        BOTTOM_UP   // last row -> row 0
    }

    /**
     * Order of slots inside each row.
     */
    public enum HorizontalOrder {
        LEFT_TO_RIGHT,   // default: col 0 -> last col
        RIGHT_TO_LEFT    // last col -> col 0
    }

    public static final PatternOptionKey<VerticalDirection> VERTICAL_DIRECTION =
            PatternOptionKey.of("rowByRow.verticalDirection");

    public static final PatternOptionKey<HorizontalOrder> HORIZONTAL_ORDER =
            PatternOptionKey.of("rowByRow.horizontalOrder");

    @Override
    public Set<PatternOptionKey<?>> supportedOptions() {
        return Set.of(VERTICAL_DIRECTION, HORIZONTAL_ORDER);
    }

    @Override
    public int[] generate(int rows, int cols, PatternOptions options) {
        VerticalDirection verticalDirection =
                options.get(VERTICAL_DIRECTION, VerticalDirection.TOP_DOWN);
        HorizontalOrder horizontalOrder =
                options.get(HORIZONTAL_ORDER, HorizontalOrder.LEFT_TO_RIGHT);

        int[] order = new int[rows * cols];
        int index = 0;

        // Traverse rows according to vertical direction
        for (int rIndex = 0; rIndex < rows; rIndex++) {
            int row = (verticalDirection == VerticalDirection.TOP_DOWN)
                    ? rIndex
                    : (rows - 1 - rIndex);

            // Traverse columns according to horizontal order
            if (horizontalOrder == HorizontalOrder.LEFT_TO_RIGHT) {
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
}
