package chess;

import chess.Board;
import chess.Index;
import chess.enums.MoveType;

public class Bishop extends Piece {
    public Bishop(boolean isLight) {
        super(isLight);
    }

    public MoveType isValidMove(Board b, Index src, Index dest) {
        return MoveType.INVALID;
    }

    public String toString() {
        return super.toString() + "Bishop";
    }

    public String toSmallString() {
        return super.toString() + "B";
    }
}
