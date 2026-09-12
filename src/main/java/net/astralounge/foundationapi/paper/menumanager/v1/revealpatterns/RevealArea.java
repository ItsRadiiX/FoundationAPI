package net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns;

public enum RevealArea {
    THREE_BY_NINE(3, 9),
    SIX_BY_NINE(6, 9),
    ONE_BY_NINE(1, 9);

    private final int rows;
    private final int cols;

    RevealArea(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }
}
