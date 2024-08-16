package chess;

import java.net.Socket;

import websocket.WebSocketServer;

import chess.GameSerializer;
import chess.enums.MoveType;
import chess.Index;
import chess.pieces.Piece;
import chess.Board;
import chess.Logic;

public class Chess implements Runnable {
    private static final int MAX_MESG_COUNT = 10;

    private Board board;
    private Logic logic;

    private WebSocketServer ws;
    private Socket client;

    public Chess() {
        this.board = new Board();
        this.logic = new Logic();
    }

    public Chess(WebSocketServer ws, Socket client) {
        this.board = new Board();
        this.logic = new Logic();

        this.ws = ws;
        this.client = client;
    }

    // this should, in time, be split into two functions, one for play, one for run
    public void run() {
        try {
            while (true) {
                System.out.println();
                String mesg = ws.readMesg(client);
                if (mesg == null) {
                    break;
                }
                System.out.println();

                for (String s : this.getInstructions(mesg)) {
                    if (s != null) { // this should always be returing at least one message
                        ws.sendMesg(client, s);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
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
            src  = new Index(-1);
            dest = new Index(-1);
            p = this.logic.processPromotion(mesgd[1], this.board, p, dest);
            m = MoveType.PROMOTION_SUBSTITUTE;
        } else if (mesgd[0].equals("RESET")) {
            System.out.println("Reseting game.");
            this.board = new Board();
            this.logic = new Logic();
            return new String[]{"RESET"};
        } else { ; } // error

        return GameSerializer.serializeMove(this.board, m, p, src, dest);
    }
}
