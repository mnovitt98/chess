package chess.pieces;

import chess.enums.MoveType;
import chess.Index;
import chess.Board;

public class Piece {
    public static boolean sameColor(Piece a, Piece b) {
        return a.isLight() == b.isLight();
    }

    private boolean isLight;

    public Piece(boolean isLight) {
        this.isLight = isLight;
    }

    public boolean isLight() {
        return this.isLight;
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

        boolean attacking = b.pieceAt(dest);
        if (attacking && Piece.sameColor(this, b.getPieceAt(dest))) {
            System.out.println("Invalid: can't capture a piece of the same color.");
            return MoveType.INVALID;
        }

        return mt;
    }
}
