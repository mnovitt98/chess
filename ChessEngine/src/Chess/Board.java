package chess;

import java.lang.ArrayIndexOutOfBoundsException;

import chess.Index;
import chess.pieces.Piece;
import chess.pieces.Pawn;
import chess.pieces.Rook;
import chess.pieces.Knight;
import chess.pieces.Bishop;
import chess.pieces.Queen;
import chess.pieces.King;

public class Board {
    private Piece[] board;

    public Board() {
        this.board = new Piece[64];
        for (int i = 0; i < 8; i++) {
            this.board[8+i] = new Pawn(false);
            this.board[64-16+i] = new Pawn(true);
        }

        this.board[0] = new Rook(false);
        this.board[64-8] = new Rook(true);
        this.board[1] = new Knight(false);
        this.board[64-8+1] = new Knight(true);
        this.board[2] = new Bishop(false);
        this.board[64-8+2] = new Bishop(true);
        this.board[3] = new Queen(false);
        this.board[64-8+3] = new Queen(true);
        this.board[4] = new King(false);
        this.board[64-8+4] = new King(true);
        this.board[5] = new Bishop(false);
        this.board[64-8+5] = new Bishop(true);
        this.board[6] = new Knight(false);
        this.board[64-8+6] = new Knight(true);
        this.board[7] = new Rook(false);
        this.board[64-8+7] = new Rook(true);

        this.printBoard();
    }

    public Piece getPieceAt(int srcIndex) {
        return (srcIndex < 0) ? null : board[srcIndex];
    }

    public Piece getPieceAt(Index src) {
        return (src.getPos() < 0) ? null : board[src.getPos()];
    }

    public Piece setPieceAt(int srcIndex, Piece p) {
        return board[srcIndex] = p;
    }

    public Piece setPieceAt(Index src, Piece p) {
        return board[src.getPos()] = p;
    }

    public boolean pieceAt(int srcIndex) {
        return this.getPieceAt(srcIndex) != null;
    }

    public boolean pieceAt(Index src) {
        return this.getPieceAt(src) != null;
    }

    public boolean isAttackingMove(Index i) {
        return this.pieceAt(i.getPos());
    }

    public boolean openWalk(Index src, Index dest, Index.Direction d) {
        int distance = -1;
        switch (d) {
        case Index.Direction.FORWARD:
            distance = src.forwardDistanceTo(dest);
            break;
        case Index.Direction.BACKWARD:
            distance = src.backwardDistanceTo(dest);
            break;
        case Index.Direction.LEFT:
            distance = src.leftDistanceTo(dest);
            break;
        case Index.Direction.RIGHT:
            distance = src.rightDistanceTo(dest);
            break;
        case Index.Direction.QUADRANT_I:
        case Index.Direction.QUADRANT_II:
        case Index.Direction.QUADRANT_III:
        case Index.Direction.QUADRANT_IV:
            distance = src.diagnolDistanceTo(dest);
            break;
        }

        if (!Index.reachable(distance)) {
            return false;
        }

        if (distance == 0) {
            return true;
        }

        try {
            for (int i = 0; i < distance - 1; i++) {

                switch (d) {
                case Index.Direction.FORWARD:
                    src = src.forward(1);
                    break;
                case Index.Direction.BACKWARD:
                    src = src.backward(1);
                    break;
                case Index.Direction.LEFT:
                    src = src.left(1);
                    break;
                case Index.Direction.RIGHT:
                    src = src.right(1);
                    break;
                case Index.Direction.QUADRANT_I:
                case Index.Direction.QUADRANT_II:
                case Index.Direction.QUADRANT_III:
                case Index.Direction.QUADRANT_IV:
                    src = src.diagnol(1, d);

                    // below is critical! big headache lies in wait if this is not understood.
                    // if we leave the diagnol that src and dest originally had, OR we are
                    // getting further away from it on the same diagnol we exit
                    if (!src.onSameDiagnol(dest) || src.diagnolDistanceTo(dest) > distance) {
                        return false;
                    }

                    break;
                }

                if (this.pieceAt(src)) {
                    return false;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            /* This should never be reached...but safeguards against an endless
               loop. */
            System.out.println(String.format("This code should be unreachable!"));
            return false;
        }

        return true;
    }

    public void printBoard() {
        System.out.println();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = this.board[i*8+j];
                if (p == null) {
                    System.out.print(" __ ");
                } else {
                    System.out.print(String.format(" %s ", p.toSmallString()));
                }
            }
            System.out.println();
        }
    }
}
