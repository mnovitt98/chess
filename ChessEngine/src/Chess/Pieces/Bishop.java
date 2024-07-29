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
        MoveType mt = super.isValidMove(b, src, dest);
        if (mt == MoveType.INVALID) {
            return mt;
        }
        mt = MoveType.INVALID;
        if (this.isLight()) {
            src = new Index(src, this);
        }

        boolean attacking = b.pieceAt(dest);
        if ((b.openWalk(src, dest, Index.Direction.QUADRANT_I))
            || (b.openWalk(src, dest, Index.Direction.QUADRANT_II))
            || (b.openWalk(src, dest, Index.Direction.QUADRANT_III))
            || (b.openWalk(src, dest, Index.Direction.QUADRANT_IV))) {
            mt = attacking ? MoveType.CAPTURE : MoveType.ADVANCE;
        }

        if (mt != MoveType.INVALID) {
            System.out.println(String.format("Bishop %s %s", attacking ? "takes" : "to", dest.inChessNotation()));
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
