package chess;

import chess.Board;
import chess.Index;
import chess.enums.MoveType;

public class King extends Piece {
    public King(boolean isLight) {
        super(isLight);
    }

    public MoveType isValidMove(Board b, Index src, Index dest) {
        return MoveType.INVALID;
    }

    public String toString() {
        return super.toString() + "King";
    }

    public String toSmallString() {
        return super.toString() + "K";
    }
}
