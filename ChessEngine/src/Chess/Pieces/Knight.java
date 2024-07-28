package chess.pieces;

import chess.Board;
import chess.Index;
import chess.enums.MoveType;

public class Knight extends Piece {
    public Knight(boolean isLight) {
        super(isLight);
    }

    public MoveType isValidMove(Board b, Index src, Index dest) {
        return MoveType.INVALID;
    }

    public String toString() {
        return super.toString() + "Knight";
    }

    public String toSmallString() {
        return super.toString() + "N";
    }
}
