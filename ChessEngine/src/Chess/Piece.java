package chess;

import chess.Board;
import chess.Index;
import chess.enums.MoveType;

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

    public abstract MoveType isValidMove(Board b, Index src, Index dest);
}
