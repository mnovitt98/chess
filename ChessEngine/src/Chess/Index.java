package chess;

import java.lang.ArrayIndexOutOfBoundsException;

public class Index {
    private int isLight;
    private int pos;
    private int numRows;
    private int numCols;

    /* Assumes board is a rectangle with index 0 being at the "top left", so
       if r == numRows and c == numCols:
       0         1          ... c - 1
       c         c + 1      ... c*2 - 1
       ...       ...        ... ...
       c(r - 1)  c(r-1) + 1 ... cr - 1
    */
    public Index(int position, boolean isLight) {
        this.pos = position;
        this.isLight = isLight ? -1 : 1;
        this.numRows = 8;
        this.numCols = 8;
    }

    public Index(int position) {
        this.pos = position;
        this.isLight = 1; /* assume index is NOT light */
        this.numRows = 8;
        this.numCols = 8;
    }

    public int getRow() {
        return Math.floorDiv(this.pos, this.numRows);
    }

    public int getCol() {
        return this.pos - (this.getRow() * this.numCols);
    }

    public int[] toPair() {
        return new int[]{getRow(), getCol()};
    }

    public boolean outOfBounds() {
        return this.pos < 0 || this.pos > (this.numRows * this.numCols);
    }

    public Index forward(int numSquares)
        throws ArrayIndexOutOfBoundsException
    {
        this.pos += this.isLight * numSquares * numRows;
        if (outOfBounds()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this;
    }

    public Index backward(int numSquares)
        throws ArrayIndexOutOfBoundsException
    {
        this.pos -= this.isLight * numSquares * numRows;
        if (outOfBounds()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this;
    }

    public Index left(int numSquares)
        throws ArrayIndexOutOfBoundsException
    {
        int oldRow = getRow();
        this.pos -= isLight * numSquares;
        if (outOfBounds() || getRow() != oldRow) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this;
    }

    public Index right(int numSquares)
        throws ArrayIndexOutOfBoundsException
    {
        int oldRow = getRow();
        this.pos += isLight * numSquares;
        if (outOfBounds() || getRow() != oldRow) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this;
    }

    public Index diagnol(int numSquares, int quadrant)
        throws ArrayIndexOutOfBoundsException
    {
        for (int i = 0; i < numSquares; i++) {
            /* Quadrants:
               II  I
               III IV
            */
            if (quadrant == 2) {
                this.backward(1).right(1);
            } else if (quadrant == 1) {
                this.backward(1).left(1);
            } else if (quadrant == 4) {
                this.forward(1).right(1);
            } else {
                this.forward(1).left(1);
            }
        }

        return this;
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

    public String toString() {
        return String.format("Row: %d, Col: %d", getRow(), getCol());
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
