package chess.pieces;

import chess.pieces.Piece;
import chess.enums.MoveType;
import chess.Index;
import chess.Board;

public class King extends Piece {
    public King(boolean isLight) {
        super(isLight);
    }

    public MoveType _isValidMove(Board b, Index src, Index dest) {
        MoveType mt = MoveType.INVALID;
        boolean attacking = b.isAttackingMove(dest);

        if (!attacking) {
            System.out.println("This is not an attacking move.");
            if (kingsideCastle(b, src, dest)) {
                System.out.println("Castling kingside.");
                return MoveType.KCASTLE;
            }
            if (queenSideCastle(b, src, dest)) {
                System.out.println("Castling queenside.");
                return MoveType.QCASTLE;
            }
        }

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

    public boolean isCastleCandidate() {
        if (this.hasMoved()) {
            System.out.println("King has already moved, invalid castle.");
            return false;
        }

        return true;
    }

    // the rook positions will be evaluated relative to white in all cases for
    // less confusion and cleaner code
    public Index getKingsideRookPos() {
        return isLight() ?
            new Index(Index.DEFAULT_DIM*Index.DEFAULT_DIM - 1, false) :
            new Index(Index.DEFAULT_DIM - 1, false);
    }

    public Index getQueensideRookPos() {
        return isLight() ?
            new Index(Index.DEFAULT_DIM*(Index.DEFAULT_DIM - 1), false) :
            new Index(0, false);
    }

    public Index getKingsideRookDest() {
        return getKingsideRookPos().left(2);
    }

    public Index getQueensideRookDest() {
        return getQueensideRookPos().right(3);
    }

    ////////////////////////////////////////////////////////////////////////////

    public boolean kingsideCastle(Board b, Index src, Index dest) {
        // this is done with white orientation in mind, could just as easily
        // be done with black orientation - right would need to be substituted for
        // left below
        src.setSwitchOrientation();

        // first order of business, check that the king and rook
        // are valid candidates for castling
        Index edgeOfBoard = new Index(src.right(3));
        Piece p = b.getPieceAt(edgeOfBoard);
        if (!this.isCastleCandidate()
            || !((p instanceof Rook) && ((Rook) p).isCastleCandidate())) {
            return false;
        }

        // we now know that the king is at its starting location,
        // that is, it is at src

        // then check open walk and check
        return dest.isKingSide()
            && src.right(2).equals(dest)
            && b.openWalk(src, edgeOfBoard, Index.Direction.RIGHT);
    }

    public boolean queenSideCastle(Board b, Index src, Index dest) {
        // this is done with white orientation in mind, could just as easily
        // be done with black orientation - right would need to be substituted for
        // left below
        src.setSwitchOrientation();

        // first order of business, check that the king and rook
        // are valid candidates for castling
        Index edgeOfBoard = new Index(src.left(4));
        Piece p = b.getPieceAt(edgeOfBoard);
        if (!this.isCastleCandidate()
            || !((p instanceof Rook) && ((Rook) p).isCastleCandidate())) {
            return false;
        }

        // we now know that the king is at its starting location,
        // that is, it is at src

        // then check open walk and check
        return !dest.isKingSide()
            && src.left(2).equals(dest)
            && b.openWalk(src, edgeOfBoard, Index.Direction.LEFT);
    }

    public String toString() {
        return super.toString() + "King";
    }

    public String toSmallString() {
        return super.toString() + "K";
    }
}
