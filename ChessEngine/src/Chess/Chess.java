package chess;

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
        /* rememeber, split takes a regex, so we need to
           escape the alternation operator */
        String[] mesgd = mesg.split("\\|");
        int srcIndex  = Integer.parseInt(mesgd[0]);
        int destIndex = Integer.parseInt(mesgd[1]);
        String piece  = mesgd[2];


        String[] mesgs = new String[MAX_MESG_COUNT];
        mesgs[0] = String.join("|", mesgd[0], mesgd[1], mesgd[2]);
        /*
        for (Move m : this.logic.processMove(srcIndex, destIndex)) {
            mesgs.add(m.toMesg());
        }
        */

        return mesgs;
    }
}
