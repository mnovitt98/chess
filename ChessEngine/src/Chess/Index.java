package chess;

import java.lang.ArrayIndexOutOfBoundsException;

public class Index {
    private static final int INFINITY    = Integer.MAX_VALUE;
    public  static final int DEFAULT_DIM = 8;
    public  static enum  Direction {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT,
        QUADRANT_I,
        QUADRANT_II,
        QUADRANT_III,
        QUADRANT_IV
    }

    public static int toC(int row, int col) {
        return row*DEFAULT_DIM + col;
    }

    public static boolean reachable(int distance) {
        return distance > 0 && distance != Index.INFINITY;
    }

    private int orientation;
    private int pos;
    private int numRows;
    private int numCols;

    /* Assumes board is a rectangle with index 0 being at the "top left", so
       if r == numRows and c == numCols:
       0         1          ... c - 1
       c         c + 1      ... c*2 - 1
       ...       ...        ... ...
       c(r - 1)  c(r-1) + 1 ... cr - 1

       Assumes "positive" motion tends from the top left to the bottom right.
    */

    /* constructors */

    // int parameters
    public Index(int position) {
        this(position, true);
    }

    public Index(int position, boolean defaultOrientation) {
        this.pos = position;
        this.orientation = defaultOrientation ? 1 : -1;
        this.numRows = this.numCols = Index.DEFAULT_DIM;
    }

    public Index(int position, boolean defaultOrientation, int numRows, int numCols) {
        this(position, defaultOrientation);
        this.numRows = numRows;
        this.numCols = numCols;
    }

    // index parameters
    public Index(Index i) {
        this.pos = i.getPos();
        this.orientation = i.getOrientation();
        this.numRows = i.getNumRows();
        this.numCols = i.getNumCols();
    }

    // does not include a constructor that intakes an index as well as row and col
    // specification - differing in dimension is too drastic a difference

    /* getters and setters */

    public int getPos() {
        return this.pos;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumCols() {
        return this.numCols;
    }

    public int getRow() {
        return Math.floorDiv(this.pos, this.numRows);
    }

    public int getCol() {
        return this.pos - (this.getRow() * this.numCols);
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void switchOrientation() {
        this.orientation *= -1;
    }

    public void setDefaultOrientation() {
        this.orientation = 1;
    }

    public void setSwitchOrientation() {
        this.orientation = -1;
    }

    /* predicates */

    public boolean equals(Index other) {
        /* this is not canonical, should accept Object type and check for specific type
           ONLY CONCERNED ABOUT LOCATION NOT ORIENTATION */
        return this.getPos() == other.getPos()
            && this.getNumRows() == other.getNumRows()
            && this.getNumCols() == other.getNumCols();
    }

    public boolean outOfBounds() {
        return this.pos < 0 || this.pos > (this.numRows * this.numCols);
    }

    public boolean onSameRow(Index other) {
        return this.getRow() == other.getRow();
    }

    public boolean onSameColumn(Index other) {
        return this.getCol() == other.getCol();
    }

    public boolean onSameDiagnol(Index other) {
        return Math.abs(this.getRow() - other.getRow())
            == Math.abs(this.getCol() - other.getCol());
    }

    public boolean isKingSide() {
        return this.getCol() >= 4;
    }

    /* methods */

    public Index forward(int numSquares) {
        Index updated = new Index(this);
        updated.setPos(this.pos + this.orientation * numSquares * this.numCols);
        if (updated.outOfBounds()) {
            return new Index(-1);
        }
        return updated;
    }

    public int forwardDistanceTo(Index i) {
        int distance = 0;
        if (!this.onSameColumn(i)) {
            distance = Index.INFINITY;
        } else {
            distance = i.getRow() - this.getRow();
        }

        return distance * this.orientation;
    }

    public Index backward(int numSquares) {
        Index updated = new Index(this);
        updated.setPos(this.pos - this.orientation * numSquares * this.numCols);
        if (updated.outOfBounds()) {
            return new Index(-1);
        }
        return updated;
    }

    public int backwardDistanceTo(Index i) {
        return forwardDistanceTo(i) * -1;
    }

    public Index left(int numSquares) {
        int oldRow = getRow();
        Index updated = new Index(this);
        updated.setPos(this.pos + this.orientation * numSquares);
        if (updated.outOfBounds() || updated.getRow() != oldRow) { /* fell off the side of the board */
            return new Index(-1);
        }
        return updated;
    }

    public int leftDistanceTo(Index i) {
        int distance = 0;
        if (!this.onSameRow(i)) {
            distance = Index.INFINITY;
        } else {
            distance = i.getCol() - this.getCol();
        }

        return distance * this.orientation;
    }

    public Index right(int numSquares) {
        int oldRow = getRow();
        Index updated = new Index(this);
        updated.setPos(this.pos - this.orientation * numSquares);
        if (updated.outOfBounds() || updated.getRow() != oldRow) { /* fell off the side of the board */
            return new Index(-1);
        }
        return updated;
    }

    public int rightDistanceTo(Index i) {
        return leftDistanceTo(i) * -1;
    }

    public Index diagnol(int numSquares, Index.Direction quadrant) {
        Index updated = new Index(this);
        switch (quadrant) {
        case Index.Direction.QUADRANT_I:
            updated.setPos(this.getPos() - (numSquares * (this.numCols + 1)));
            break;
        case Index.Direction.QUADRANT_II:
            updated.setPos(this.getPos() - (numSquares * (this.numCols - 1)));
            break;
        case Index.Direction.QUADRANT_III:
            updated.setPos(this.getPos() + (numSquares * (this.numCols - 1)));
            break;
        case Index.Direction.QUADRANT_IV:
            updated.setPos(this.getPos() + (numSquares * (this.numCols + 1)));
            break;
        }

        if (updated.outOfBounds()){
            return new Index(-1);
        }

        return updated;
    }

    public int diagnolDistanceTo(Index i) {
        int distance = 0;
        if (!this.onSameDiagnol(i)) {
            distance = Index.INFINITY;
        } else {
            distance = Math.abs(this.getRow() - i.getRow());
        }

        return distance;
    }

    /* representation */

    public String inChessNotation() {
        /* remember row, col starts at "top left" of chess board */
        return String.format("%c%d", 'a' + this.getCol(), (8 - this.getRow()));
    }

    public int[] toPair() {
        return new int[]{getRow(), getCol()};
    }

    public String toString() {
        return String.format("%d", getPos());
    }

    public void printOnBoard() {
        System.out.println();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if ((i*numCols + j) == this.pos) {
                    System.out.print("*");
                } else {
                    System.out.print("X");
                }
            }
            System.out.println();
        }
    }
}
