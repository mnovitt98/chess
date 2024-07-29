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
        MoveType mt = super.isValidMove(b, src, dest);
        if (mt == MoveType.INVALID) {
            return mt;
        }
        mt = MoveType.INVALID;
        if (this.isLight()) {
            src = new Index(src, this);
        }

        boolean attacking = b.pieceAt(dest);
        if ((new Rook(this.isLight()).isValidMove(b, src, dest)) != MoveType.INVALID
            || (new Bishop(this.isLight()).isValidMove(b, src, dest)) != MoveType.INVALID) {
            mt = attacking ? MoveType.CAPTURE : MoveType.ADVANCE;
        }

        if (mt != MoveType.INVALID) {
            System.out.println(String.format("Queen %s %s", attacking ? "takes" : "to", dest.inChessNotation()));
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
