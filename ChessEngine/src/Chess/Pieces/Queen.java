package chess.pieces;

import chess.pieces.Piece;
import chess.enums.MoveType;
import chess.Index;
import chess.Board;

public class Queen extends Piece {
    public Queen(boolean isLight) {
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

        // should try to reuse rook and bishop code here, same with king
        if (((b.openWalk(src, dest, Index.Direction.BACKWARD))
            || (b.openWalk(src, dest, Index.Direction.FORWARD))
            || (b.openWalk(src, dest, Index.Direction.LEFT))
            || (b.openWalk(src, dest, Index.Direction.RIGHT)))
            || (b.openWalk(src, dest, Index.Direction.QUADRANT_I))
            || (b.openWalk(src, dest, Index.Direction.QUADRANT_II))
            || (b.openWalk(src, dest, Index.Direction.QUADRANT_III))
            || (b.openWalk(src, dest, Index.Direction.QUADRANT_IV))) {
            System.out.println(String.format("Queen %s %s", attacking ? "takes" : "to", dest.inChessNotation()));
            mt = attacking ? MoveType.CAPTURE : MoveType.ADVANCE;
        }

        return mt;
    }

    public String toString() {
        return super.toString() + "Queen";
    }

    public String toSmallString() {
        return super.toString() + "Q";
    }
}
