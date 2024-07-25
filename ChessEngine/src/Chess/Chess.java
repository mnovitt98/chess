package chess;

import chess.Board;
import chess.Logic;
import chess.enums.MoveType;
import chess.Index;

public class Chess {
    private static final int MAX_MESG_COUNT = 10;

    private Board board;
    private Logic logic;

    public Chess() {
        this.board = new Board();
        this.logic = new Logic();
    }

    private String[] translateMove(MoveType m, int srcIndex, int destIndex, Piece p) {
        switch (m) {
        case MoveType.CAPTURE:
        case MoveType.ADVANCE:
            this.board.setPieceAt(srcIndex, null);
            this.board.setPieceAt(destIndex, p);
            this.board.printBoard();
            return new String[]{String.join("|",
                                            Integer.toString(srcIndex),
                                            Integer.toString(destIndex),
                                            p.toString())};
        case MoveType.LENPASSANT:
            this.board.setPieceAt((new Index(srcIndex)).left(1), null);
            this.board.setPieceAt(destIndex, p);
            this.board.printBoard();
            return new String[]{
                String.join("|",
                            Integer.toString(srcIndex),
                            Integer.toString(destIndex),
                            p.toString()),
                String.join("|",
                            Integer.toString((new Index(srcIndex, p.isLight())).left(1).getPos()),
                            Integer.toString((new Index(srcIndex, p.isLight())).left(1).getPos()),
                            "")
            };
        }

        return new String[]{"||INVALID"};
    }

    public String[] getInstructions(String mesg) {
        /* rememeber, split takes a regex, so we need to
           escape the alternation operator */
        String[] mesgd = mesg.split("\\|");
        int srcIndex  = Integer.parseInt(mesgd[0]);
        int destIndex = Integer.parseInt(mesgd[1]);
        Piece p = this.board.getPieceAt(srcIndex);

        MoveType m = this.logic.processMove(this.board, srcIndex, destIndex);
        return this.translateMove(m, srcIndex, destIndex, p);
    }
}
