package chess;

import chess.Board;
import chess.Piece;
import chess.Index;
import chess.enums.MoveType;

public class Logic {
    public static MoveType processMove(Board board, int srcIndex, int destIndex) {
        Piece src = board.getPieceAt(srcIndex);
        if (src == null) {
            return MoveType.INVALID;
        }

        return src.isValidMove(board, new Index(srcIndex), new Index(destIndex));
    }

    public Logic() {}
}
