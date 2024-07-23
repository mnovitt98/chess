package chess;

import chess.Piece;
import chess.enums.MoveType;
import chess.Board;

public class Pawn extends Piece {
    public Piece(boolean isDark) {
        super(isDark);
    }

    public MoveType isValidMove(Board b, int srcIndex, destIndex) {
        MoveType mt = null;
        if (b.pieceAt(srcIndex)) {
            // either regular capture
        }

        switch () {
            case :  ;
                break;
    }
}
