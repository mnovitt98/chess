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
        if (this.isLight()) {
            src = new Index(src, this);
        }

        MoveType mt = MoveType.INVALID;
        if (src.outOfBounds() || dest.outOfBounds()) {
            return mt;
        }

        boolean attacking = b.pieceAt(dest);
        if (attacking && Piece.sameColor(this, b.getPieceAt(dest))) {
            System.out.println("Invalid: Rook can't capture a piece of the same color.");
            return MoveType.INVALID;
        }

        if (src.forward(2).left(1).equals(dest)
            || src.forward(2).right(1).equals(dest)
            || src.backward(2).left(1).equals(dest)
            || src.backward(2).right(1).equals(dest)
            || src.left(2).forward(1).equals(dest)
            || src.left(2).backward(1).equals(dest)
            || src.right(2).forward(1).equals(dest)
            || src.right(2).backward(1).equals(dest)) {
            System.out.println(String.format("Knight %s %s", attacking ? "takes" : "to", dest.inChessNotation()));
            mt = attacking ? MoveType.CAPTURE : MoveType.ADVANCE;
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
