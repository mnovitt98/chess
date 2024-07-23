package chess;

import chess.Board;
import chess.enums.MoveType;

public abstract class Piece {
    private boolean isLight;

    public Piece(boolean isLight) {
        this.isLight = isLight;
    }

    public abstract MoveType isValid(Board b, int srcIndex, int destIndex);
}
