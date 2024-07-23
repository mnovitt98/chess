import java.io.IOException;

import websocket.WebSocketServer;
import chess.Chess;

public class Driver {
    private static final int PORT = 7897;
    public static void main(String[] args) {
        WebSocketServer ws;
        try {
            ws = new WebSocketServer(PORT);
            System.out.println("Awaiting client connection...");
        } catch (IOException e) {
            System.out.print(String.format("Exception setting up socket server: %s\n", e)
                             + "Quitting.\n");
            System.exit(1);
            return; /* this is unreachable, but placates the compiler warning that ws may
                       be uninitialized in the following statement. */
        }

        /* this is blocking */
        ws.getClientandUpgradeConnection();

        Chess game = new Chess();
        while (true) {
            /* this is blocking */
            String mesg = ws.readMesg();

            for (String s : game.getInstructions(mesg)) {
                if (s != null) {
                    /* this is blocking */
                    ws.sendMesg(s);
                }
            }
        }
    }
}
