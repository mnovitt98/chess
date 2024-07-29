package chess.pieces;

import chess.pieces.Piece;
import chess.enums.MoveType;
import chess.Index;
import chess.Board;

public class Bishop extends Piece {
    public Bishop(boolean isLight) {
        super(isLight);
    }

    public MoveType isValidMove(Board b, Index src, Index dest) {
        if (this.isLight()) {
            src = new Index(src, this);
        }

        MoveType mt = MoveType.INVALID;
        if (src.outOfBounds() || dest.outOfBounds()) {
            return mt;
        }

        boolean attacking = b.pieceAt(dest);
        if (attacking && Piece.sameColor(this, b.getPieceAt(dest))) {
            System.out.println("Invalid: Bishop can't capture a piece of the same color.");
            return MoveType.INVALID;
        }

        if ((b.openWalk(src, dest, Index.Direction.QUADRANT_I))
            || (b.openWalk(src, dest, Index.Direction.QUADRANT_II))
            || (b.openWalk(src, dest, Index.Direction.QUADRANT_III))
            || (b.openWalk(src, dest, Index.Direction.QUADRANT_IV))) {
            System.out.println(String.format("Bishop %s %s", attacking ? "takes" : "to", dest.inChessNotation()));
            mt = attacking ? MoveType.CAPTURE : MoveType.ADVANCE;
        }

        return mt;
    }

    public String toString() {
        return super.toString() + "Bishop";
    }

    public String toSmallString() {
        return super.toString() + "B";
    }
}
