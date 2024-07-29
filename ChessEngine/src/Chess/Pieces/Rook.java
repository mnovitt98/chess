package chess.pieces;

import chess.pieces.Piece;
import chess.enums.MoveType;
import chess.Index;
import chess.Board;

public class Rook extends Piece {
    public Rook(boolean isLight) {
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
        if ((b.openWalk(src, dest, Index.Direction.BACKWARD))
            || (b.openWalk(src, dest, Index.Direction.FORWARD))
            || (b.openWalk(src, dest, Index.Direction.LEFT))
            || (b.openWalk(src, dest, Index.Direction.RIGHT))) {
            mt = attacking ? MoveType.CAPTURE : MoveType.ADVANCE;
        }

        if (mt != MoveType.INVALID) {
            System.out.println(String.format("Rook %s %s", attacking ? "takes" : "to", dest.inChessNotation()));
        }

        return mt;
    }

    public String toString() {
        return super.toString() + "Rook";
    }

    public String toSmallString() {
        return super.toString() + "R";
    }
}
