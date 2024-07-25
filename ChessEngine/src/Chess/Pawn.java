package chess;

import chess.enums.MoveType;
import chess.Piece;
import chess.Board;
import chess.Index;

public class Pawn extends Piece {
    private static boolean canBeTakenEnPassant(Piece p) {
        System.out.print(p);
        if (p == null) {
            return false;
        }
        Pawn pa = (Pawn) p;
        System.out.println("here");
        return pa.wasEager() && pa.getMoveCount() == 1;
    }

    private int moveCount;
    private boolean wasEager;

    public Pawn(boolean isDark) {
        super(isDark);
        this.moveCount = 0;
        this.wasEager = false;
    }

    public int getMoveCount() {
        return this.moveCount;
    }

    public boolean hasMoved() {
        return this.getMoveCount() > 0;
    }

    public boolean wasEager() {
        return this.wasEager;
    }

    public MoveType isValidMove(Board b, Index src, Index dest) {
        /* need to put this in super so it isnt repeated everywhere */
        if (this.isLight()) {
            src = new Index(src, true);
        }

        MoveType mt = MoveType.INVALID;
        if (src.outOfBounds() || dest.outOfBounds()) {
            return mt;
        }

        if (b.pieceAt(dest)) { /* attacking */
            if (!Piece.sameColor(this, b.getPieceAt(dest))) {
                // normal capture
                if (b.pieceAt(src.forward(1).left(1)) == b.pieceAt(dest)
                    || b.pieceAt(src.forward(1).right(1)) == b.pieceAt(dest)) {
                    mt = MoveType.CAPTURE;
                }
            } else { /* cant capture a piece of the same color... */
                return MoveType.INVALID;
            }
        } else {
            System.out.println(String.format("%s %s", src, dest));
            System.out.println(String.format("%s %s", src.forward(1), src.forward(1).left(1)));
                // 1. regular advance
            if (src.forward(1).equals(dest)) {
                mt = MoveType.ADVANCE;
                // 2. move 2 from start
            } else if (!this.hasMoved()
                       && src.forwardDistanceTo(dest) == 2
                       && b.openForwardWalk(src, dest)) {
                this.wasEager = true;
                mt = MoveType.ADVANCE;
                // 3. en passant left
            } else if (src.forward(1).left(1).equals(dest)) {
                /*                && Pawn.canBeTakenEnPassant(b.getPieceAt(src.left(1)))) {*/
                System.out.print(String.format("here %s", src.left(1)));
                System.out.print(b.getPieceAt(src.left(1)));
                mt = MoveType.LENPASSANT;
                // 4. en passant right
            } else if (src.forward(1).right(1).equals(dest)
                && Pawn.canBeTakenEnPassant(b.getPieceAt(src.right(1)))) {
                System.out.println("in r en");
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
