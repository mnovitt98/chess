package chess.pieces;

import chess.enums.MoveType;
import chess.pieces.Piece;
import chess.Index;
import chess.Index;
import chess.Board;

public class Pawn extends Piece {
    private static boolean canBeTakenEnPassant(Piece p) {
        if (p == null) {
            return false;
        }
        Pawn pa = (Pawn) p;
        return pa.wasEager() && pa.getMoveCount() == 1;
    }

    private boolean wasEager;

    public Pawn(boolean isDark) {
        super(isDark);
        this.wasEager = false;
    }

    public boolean wasEager() {
        return this.wasEager;
    }

    public MoveType _isValidMove(Board b, Index src, Index dest) {
        MoveType mt = MoveType.INVALID;
        if (b.pieceAt(dest)) { /* standard attacking */
            // normal capture
            if (b.pieceAt(src.forward(1).left(1)) == b.pieceAt(dest)
                || b.pieceAt(src.forward(1).right(1)) == b.pieceAt(dest)) {
                System.out.println(String.format("Pawn takes %s.", dest.inChessNotation()));
                mt = MoveType.CAPTURE;
                if (dest.isBackRank()) { // check if this is a pawn promotion
                    System.out.println(String.format("Promotion now available for %s.", this.isLight() ? "White" : "Black"));
                    mt = MoveType.PROMOTION_INITIATE;
                }
            }
        } else {
            if (src.forward(1).equals(dest)) { // regular advance
                if (src.forward(1).isBackRank()) { // check if this is a pawn promotion
                    System.out.println(String.format("Pawn to %s, promotion available.", dest.inChessNotation()));
                    mt = MoveType.PROMOTION_INITIATE;
                } else {
                    System.out.println(String.format("Pawn to %s.", dest.inChessNotation()));
                    mt = MoveType.ADVANCE;
                }
            } else if (!this.hasMoved()
                       && src.forwardDistanceTo(dest) == 2
                       && b.openWalk(src, dest, Index.Direction.FORWARD)) { // move 2 from start
                System.out.println(String.format("Pawn to %s.", dest.inChessNotation()));
                this.wasEager = true;
                mt = MoveType.ADVANCE;
            } else if (src.forward(1).left(1).equals(dest)
                && Pawn.canBeTakenEnPassant(b.getPieceAt(src.left(1)))) { // en passant left
                System.out.println(String.format("Pawn takes %s.", dest.inChessNotation()));
                mt = MoveType.LENPASSANT;
            } else if (src.forward(1).right(1).equals(dest)
                && Pawn.canBeTakenEnPassant(b.getPieceAt(src.right(1)))) { // en passant right
                System.out.println(String.format("Pawn takes %s.", dest.inChessNotation()));
                mt = MoveType.RENPASSANT;
            }
        }

        return mt;
    }

    public String toString() {
        return super.toString() + "Pawn";
    }

    public String toSmallString() {
        return super.toString() + "P";
    }
}
