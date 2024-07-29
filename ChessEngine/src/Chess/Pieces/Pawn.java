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

    public MoveType isValidMove(Board b, Index src, Index dest) {
        MoveType mt = super.isValidMove(b, src, dest);
        if (mt == MoveType.INVALID) {
            return mt;
        }
        mt = MoveType.INVALID;
        if (this.isLight()) {
            src = new Index(src, this);
        }

        if (b.pieceAt(dest)) { /* standard attacking */
            if (!Piece.sameColor(this, b.getPieceAt(dest))) {
                // normal capture
                if (b.pieceAt(src.forward(1).left(1)) == b.pieceAt(dest)
                    || b.pieceAt(src.forward(1).right(1)) == b.pieceAt(dest)) {
                    System.out.println(String.format("Pawn takes %s.", dest.inChessNotation()));
                    mt = MoveType.CAPTURE;
                }
            } else {
                System.out.println("Invalid: pawn can't capture a piece of the same color.");
                return MoveType.INVALID;
            }
        } else {
            if (src.forward(1).equals(dest)) { // regular advance
                System.out.println(String.format("Pawn to %s.", dest.inChessNotation()));
                mt = MoveType.ADVANCE;
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

        if (mt != MoveType.INVALID) {
            this.moveCount++;
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
