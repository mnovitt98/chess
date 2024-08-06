package chess;

import chess.enums.MoveType;
import chess.Index;
import chess.pieces.Piece;
import chess.Board;

public class Logic {
    private static boolean DEBUG = true;

    public static MoveType processRegularMove(Board b, Index src, Index dest) {
        Piece p = b.getPieceAt(src);
        if (src == null) {
            return MoveType.INVALID;
        }

        MoveType m = p.isValidMove(b, src, dest);

        /* this will eventually also need to add pieces to their respective
           "graveyards". board should track this state */

        if (p.isLight()) {
            src.setSwitchOrientation();
        }

        switch (m) {
        case MoveType.CAPTURE:
        case MoveType.ADVANCE:
            b.setPieceAt(src, null);
            b.setPieceAt(dest, p);
            break;
        case MoveType.LENPASSANT:
            b.setPieceAt(dest, p);
            b.setPieceAt(src, null);
            b.setPieceAt(src.left(1), null);
            break;
        case MoveType.RENPASSANT:
            b.setPieceAt(dest, p);
            b.setPieceAt(src, null);
            b.setPieceAt(src.right(1), null);
            break;
        }

        if (Logic.DEBUG) {
            b.printBoard();
        }

        return m;
    }

    /* TODO */
    // public static MoveType processPromotion()

    public Logic() {}
}
