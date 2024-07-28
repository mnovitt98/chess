package chess.pieces;

import chess.Board;
import chess.Index;
import chess.enums.MoveType;

public class Queen extends Piece {
    public Queen(boolean isLight) {
        super(isLight);
    }

    public MoveType isValidMove(Board b, Index src, Index dest) {
        return MoveType.INVALID;
    }

    public String toString() {
        return super.toString() + "Queen";
    }

    public String toSmallString() {
        return super.toString() + "Q";
    }
}
