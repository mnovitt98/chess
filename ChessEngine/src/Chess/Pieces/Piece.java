package chess.pieces;

import chess.enums.MoveType;
import chess.Index;
import chess.Board;

public class Piece {
    public static boolean sameColor(Piece a, Piece b) {
        return a.isLight() == b.isLight();
    }

    protected boolean isLight;
    protected int moveCount;

    public Piece(boolean isLight) {
        this.isLight = isLight;
        this.moveCount = 0;
    }

    public boolean isLight() {
        return this.isLight;
    }

    public boolean hasMoved() {
        return this.getMoveCount() > 0;
    }

    public int getMoveCount() {
        return this.moveCount;
    }

    public String toString() {
        return this.isLight() ? "l" : "d";
    }

    public String toSmallString() {
        return this.isLight() ? "l" : "d";
    }

    public MoveType isValidMove(Board b, Index src, Index dest) {
        MoveType mt = null;
        if (src.outOfBounds() || dest.outOfBounds()) {
            return MoveType.INVALID;
        }

        if (b.pieceAt(dest) && Piece.sameColor(this, b.getPieceAt(dest))) {
            System.out.println("Invalid: can't capture a piece of the same color.");
            return MoveType.INVALID;
        }

        return mt;
    }

    // need some method that does what isValidMove is already doing, but also call the child or "next"
    // is valid move with the src index switched to the right orientation for the piece
}
