package chess;

import java.lang.ArrayIndexOutOfBoundsException;

import chess.Index;
import chess.pieces.Piece;
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
        return (srcIndex < 1) ? null : board[srcIndex];
    }

    public Piece getPieceAt(Index src) {
        return (src.getPos() < 1) ? null : board[src.getPos()];
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

    public boolean openForwardWalk(Index src, Index dest) {
        int distance = src.forwardDistanceTo(dest);
        if (!Index.reachable(distance)) {
            return false;
        }

        if (distance == 0) {
            return true;
        }

        try {
            for (int i = 0; i < distance - 1; i++) {
                src = src.forward(1);
                if (this.pieceAt(src)) {
                    return false;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            /* This should never be reached...but safeguards against an endless
               loop. */
            System.out.println(String.format("This code should be unreachable! Exception: %d", e));
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
