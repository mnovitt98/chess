
/* make this part of the chess package, and then make staic var in index protected */

import java.lang.ArrayIndexOutOfBoundsException;

import chess.Index;
import chess.Board;

public class TestIndex {
    public static void main(String[] args) {
        runSuite();
    }

    public static void runSuite() {
        Index i = new Index(0);
        try {
            i.backward(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(String.format("Index %s is out of bounds on board with dimensions: %s",
                                             i, i.toStringDim()));
        }
        i = new Index(8*8-1);
        try {
            i.forward(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(String.format("Index %s is out of bounds on board with dimensions: %s",
                                             i, i.toStringDim()));
        }

        /* positional queries */
        assert (new Index(0)).onSameRow(new Index(7));
        assert !(new Index(0)).onSameRow(new Index(8));
        assert (new Index(0)).onSameColumn(new Index(8));
        assert !(new Index(0)).onSameColumn(new Index(7));
        assert (new Index(0)).onSameDiagnol(new Index(8*8-1));
        assert !(new Index(0)).onSameDiagnol(new Index(1));

        /* distance */
        int distance = 2;
        int src  = 26;
        int dest = src + Index.DEFAULT_DIM*distance;
        assert (new Index(src)).forwardDistanceTo(new Index(dest)) == distance
            && (new Index(dest)).forwardDistanceTo(new Index(src)) == distance;

        assert (new Index(src)).backwardDistanceTo(new Index(dest)) == distance * -1
            && (new Index(dest)).backwardDistanceTo(new Index(src)) == distance * -1;
    }
}

/*
    public boolean outOfBounds()
    public boolean onSameRow(Index other)
    public boolean onSameColumn(Index other)
    public boolean onSameDiagnol(Index other)
    public boolean reachable(int distance)

    public Index forward(int numSquares)
    public int forwardDistanceTo(Index i)
    public Index backward(int numSquares)
    public int backwardDistanceTo(Index i)
    public Index left(int numSquares)
    public int leftDistanceTo(Index i)
    public Index right(int numSquares)
    public int rightDistanceTo(Index i)
    public Index diagnol(int numSquares, int quadrant)
    public int diagnolDistanceTo()
*/
