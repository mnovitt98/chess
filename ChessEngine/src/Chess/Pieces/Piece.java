package chess.pieces;

import chess.enums.MoveType;
import chess.Index;
import chess.Board;

public abstract class Piece {
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
        // base checks pertinent to all piece types
        if (src.outOfBounds() || dest.outOfBounds()) {
            return MoveType.INVALID;
        }

        if (b.pieceAt(dest) && Piece.sameColor(this, b.getPieceAt(dest))) {
            System.out.println("Invalid: can't capture a piece of the same color.");
            return MoveType.INVALID;
        }

        if (this.isLight()) {
            src.setSwitchOrientation();
        } // now _isValidMove will receive src correctly oriented

        MoveType mt = _isValidMove(b, src, dest);
        if (mt != MoveType.INVALID) {
            this.moveCount++;
        }

        return mt;
    }

    public abstract MoveType _isValidMove(Board b, Index src, Index dest);
}
