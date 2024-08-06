package chess.pieces;

import chess.pieces.Piece;
import chess.enums.MoveType;
import chess.Index;
import chess.Board;

public class King extends Piece {
    public King(boolean isLight) {
        super(isLight);
    }

    public MoveType isValidMove(Board b, Index src, Index dest) {
        MoveType mt = super.isValidMove(b, src, dest);
        if (mt == MoveType.INVALID) {
            return mt;
        }
        mt = MoveType.INVALID;
        if (this.isLight()) {
            src.setSwitchOrientation();
        }

        /* handle castling */

        boolean attacking = b.isAttackingMove(dest);
        if (src.forward(1).equals(dest)
            || src.backward(1).equals(dest)
            || src.left(1).equals(dest)
            || src.right(1).equals(dest)
            || src.diagnol(1, Index.Direction.QUADRANT_I).equals(dest)
            || src.diagnol(1, Index.Direction.QUADRANT_II).equals(dest)
            || src.diagnol(1, Index.Direction.QUADRANT_III).equals(dest)
            || src.diagnol(1, Index.Direction.QUADRANT_IV).equals(dest)){
            System.out.println(String.format("King %s %s", attacking ? "takes" : "to", dest.inChessNotation()));
            mt = attacking ? MoveType.CAPTURE : MoveType.ADVANCE;
        }

        return mt;
    }

    public String toString() {
        return super.toString() + "King";
    }

    public String toSmallString() {
        return super.toString() + "K";
    }
}
