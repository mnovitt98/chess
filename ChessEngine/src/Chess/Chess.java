package chess;

import chess.GameSerializer;
import chess.enums.MoveType;
import chess.Index;
import chess.pieces.Piece;
import chess.Board;
import chess.Logic;

public class Chess {
    private static final int MAX_MESG_COUNT = 10;

    private Board board;
    private Logic logic;

    public Chess() {
        this.board = new Board();
        this.logic = new Logic();
    }

    public String[] getInstructions(String mesg) {
        String[] mesgd = GameSerializer.deserializeMove(mesg);

        Index src = null;
        Index dest = null;
        Piece p = null;
        MoveType m = null;

        if (mesgd[0].equals("MOVE")) {
            src  = new Index(Integer.parseInt(mesgd[1]));
            dest = new Index(Integer.parseInt(mesgd[2]));
            p = this.board.getPieceAt(src);
            m = this.logic.processRegularMove(this.board, src, dest);
        } else if (mesgd[0].equals("PROMOTE")) {
            // TODO
            ;
        } else if (mesgd[0].equals("RESET")) {
            System.out.println("Reseting game.");
            this.board = new Board();
            this.logic = new Logic();
            return new String[]{"RESET"};
        } else {
            // error
            ;
        }

        return GameSerializer.serializeMove(this.board, m, p, src, dest);
    }
}
