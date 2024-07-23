package chess;

import chess.Piece;

public class Board {
    Piece[] _board;

    public Board() {
        this._board = new Piece[64];
    }

    public Piece pieceAt(int srcIndex) {
        return _board[srcIndex];
    }
}
