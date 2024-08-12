package chess;

import chess.enums.MoveType;
import chess.Index;
import chess.pieces.Piece;
import chess.pieces.King;
import chess.pieces.Queen;
import chess.pieces.Bishop;
import chess.pieces.Knight;
import chess.pieces.Rook;
import chess.Board;

public class Logic {
    private static boolean DEBUG = true;

    public static Piece pieceFromString(String s, boolean isLight) {
        switch (s) {
        case "ROOK":
            return new Rook(isLight);
        case "KNIGHT":
            return new Knight(isLight);
        case "BISHOP":
            return new Bishop(isLight);
        case "QUEEN":
            return new Queen(isLight);
        }

        return null;
    }

    private boolean turn;
    private boolean winner;
    private Index promotionPosition;

    public Logic() {
        this.turn = true; // white
        this.winner = false;
        this.promotionPosition = null;
    }

    public MoveType processRegularMove(Board b, Index src, Index dest) {
        Piece p = b.getPieceAt(src);
        if (src == null) {
            return MoveType.INVALID;
        }

        if (p.isLight() != turn) {
            System.out.println(String.format("Wrong player. %s to play.", this.turn ? "White" : "Black"));
            return MoveType.INVALID;
        }

        if (this.promotionPosition != null) { // in the middle of a promotion udpate
            System.out.println(String.format("Must continue with %s's pawn promotion.", this.turn ? "White" : "Black"));
            return MoveType.INVALID;
        }

        MoveType m = p.isValidMove(b, src, dest);

        /* this will eventually also need to add pieces to their respective
           "graveyards". board should track this state */

        if (p.isLight()) {
            src.setSwitchOrientation();
        }

        Index rsrc, rdst;
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
        case MoveType.KCASTLE:
        case MoveType.QCASTLE:
            b.setPieceAt(dest, p);
            b.setPieceAt(src, null);
            if (m == MoveType.KCASTLE) {
                rsrc = ((King) p).getKingsideRookPos();
                rdst = ((King) p).getKingsideRookDest();
            } else {
                rsrc = ((King) p).getQueensideRookPos();
                rdst = ((King) p).getQueensideRookDest();
            }
            b.setPieceAt(rdst, b.getPieceAt(rsrc));
            b.setPieceAt(rsrc, null);
            break;
        case MoveType.PROMOTION_INITIATE:
            b.setPieceAt(src, null);
            b.setPieceAt(dest, p);
            this.promotionPosition = dest;
            break;
        }

        if (!(m == MoveType.INVALID || m == MoveType.PROMOTION_INITIATE)) {
            this.turn = !this.turn;
            System.out.println(String.format("%s to play.", this.turn ? "White" : "Black"));
        }

        if (Logic.DEBUG) {
            b.printBoard();
        }

        return m;
    }

    public Piece processPromotion(String sp, Board b, Piece p, Index dest) {
        System.out.println(String.format("Promotion to %s", sp));

        p = Logic.pieceFromString(sp, this.turn);
        dest.setFromIndex(this.promotionPosition);
        b.setPieceAt(dest, p);

        this.promotionPosition = null;
        this.turn = !this.turn;
        System.out.println(String.format("%s to play.", this.turn ? "White" : "Black"));

        return p;
    }
}
