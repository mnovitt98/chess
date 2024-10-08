package chess.pieces;

import chess.pieces.Piece;
import chess.enums.MoveType;
import chess.Index;
import chess.Board;

public class Rook extends Piece {
    public Rook(boolean isLight) {
        super(isLight);
    }

    public MoveType _isValidMove(Board b, Index src, Index dest) {
        MoveType mt = MoveType.INVALID;
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

    public boolean isCastleCandidate() {
        if (this.hasMoved()) {
            System.out.println("The rook has already moved, invalid castle.");
            return false;
        }

        return true;
    }

    public String toString() {
        return super.toString() + "Rook";
    }

    public String toSmallString() {
        return super.toString() + "R";
    }
}
