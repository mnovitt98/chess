package chess.pieces;

import chess.enums.MoveType;
import chess.Index;
import chess.Board;

public abstract class Piece {
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

    public abstract MoveType isValidMove(Board board, Index src, Index dest);
}
