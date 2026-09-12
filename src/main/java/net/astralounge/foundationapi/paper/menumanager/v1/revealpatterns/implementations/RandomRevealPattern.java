package net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.implementations;

import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.PatternOptionKey;
import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.PatternOptions;
import net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns.RevealPattern;

import java.util.*;

public class RandomRevealPattern implements RevealPattern {

    /**
     * How randomness is applied to the grid.
     */
    public enum Mode {
        /**
         * Classic random animation: shuffle all slots together.
         */
        FULL,

        /**
         * Rows stay in order (0..rows-1), but each row's columns are shuffled independently.
         */
        PER_ROW,

        /**
         * Columns stay in order (0..cols-1), but each column's rows are shuffled independently.
         */
        PER_COLUMN
    }

    /**
     * Option: mode of randomization.
     */
    public static final PatternOptionKey<Mode> MODE =
            PatternOptionKey.of("random.mode");

    /**
     * Option: RNG seed. If provided, the pattern is deterministic
     * for the same (rows, cols, options).
     */
    public static final PatternOptionKey<Long> SEED =
            PatternOptionKey.of("random.seed");

    @Override
    public Set<PatternOptionKey<?>> supportedOptions() {
        return Set.of(MODE, SEED);
    }

    @Override
    public int[] generate(int rows, int cols, PatternOptions options) {
        Mode mode = options.get(MODE, Mode.FULL);
        Long seed = options.get(SEED, null);

        Random random = (seed != null) ? new Random(seed) : new Random();

        return switch (mode) {
            case FULL -> generateFullRandom(rows, cols, random);
            case PER_ROW -> generatePerRowRandom(rows, cols, random);
            case PER_COLUMN -> generatePerColumnRandom(rows, cols, random);
        };
    }

    /**
     * Shuffle all slots together.
     */
    private int[] generateFullRandom(int rows, int cols, Random random) {
        int totalSlots = rows * cols;
        List<Integer> indices = new ArrayList<>(totalSlots);
        for (int i = 0; i < totalSlots; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, random);

        int[] order = new int[totalSlots];
        for (int i = 0; i < totalSlots; i++) {
            order[i] = indices.get(i);
        }
        return order;
    }

    /**
     * Rows in order, but columns shuffled within each row.
     */
    private int[] generatePerRowRandom(int rows, int cols, Random random) {
        int[] order = new int[rows * cols];
        int index = 0;

        for (int row = 0; row < rows; row++) {
            List<Integer> rowSlots = new ArrayList<>(cols);
            for (int col = 0; col < cols; col++) {
                rowSlots.add(row * cols + col);
            }
            Collections.shuffle(rowSlots, random);
            for (int slot : rowSlots) {
                order[index++] = slot;
            }
        }

        return order;
    }

    /**
     * Columns in order, but rows shuffled within each column.
     */
    private int[] generatePerColumnRandom(int rows, int cols, Random random) {
        int[] order = new int[rows * cols];
        int index = 0;

        for (int col = 0; col < cols; col++) {
            List<Integer> colSlots = new ArrayList<>(rows);
            for (int row = 0; row < rows; row++) {
                colSlots.add(row * cols + col);
            }
            Collections.shuffle(colSlots, random);
            for (int slot : colSlots) {
                order[index++] = slot;
            }
        }

        return order;
    }
}
