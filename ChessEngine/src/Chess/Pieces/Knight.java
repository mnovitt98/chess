package chess.pieces;

import chess.pieces.Piece;
import chess.enums.MoveType;
import chess.Index;
import chess.Board;

public class Knight extends Piece {
    public Knight(boolean isLight) {
        super(isLight);
    }

    public MoveType isValidMove(Board b, Index src, Index dest) {
        MoveType mt = super.isValidMove(b, src, dest);
        if (mt == MoveType.INVALID) {
            return mt;
        }
        if (this.isLight()) {
            src = new Index(src, this);
        }

        boolean attacking = b.pieceAt(dest);
        if (src.forward(2).left(1).equals(dest)
            || src.forward(2).right(1).equals(dest)
            || src.backward(2).left(1).equals(dest)
            || src.backward(2).right(1).equals(dest)
            || src.left(2).forward(1).equals(dest)
            || src.left(2).backward(1).equals(dest)
            || src.right(2).forward(1).equals(dest)
            || src.right(2).backward(1).equals(dest)) {
            mt = attacking ? MoveType.CAPTURE : MoveType.ADVANCE;
        }

        if (mt != MoveType.INVALID) {
            System.out.println(String.format("Knight %s %s", attacking ? "takes" : "to", dest.inChessNotation()));
        }

        return mt;
    }

    public String toString() {
        return super.toString() + "Knight";
    }

    public String toSmallString() {
        return super.toString() + "N";
    }
}
