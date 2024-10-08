import java.io.IOException;
import java.net.Socket;

import websocket.WebSocketServer;
import chess.Chess;

public class Driver {
    private static final int PORT = 7897;

    public static void main(String[] args) {
        WebSocketServer ws;
        try {
            ws = new WebSocketServer(PORT);
        } catch (IOException e) {
            System.out.print(String.format("Exception setting up socket server: %s\n", e)
                             + "Quitting.\n");
            System.exit(1);
            return; /* this is unreachable, but placates the compiler warning that ws may
                       be uninitialized in the following statement. */
        }

        while (true) {
            System.out.println("Awaiting client connection...");
            Socket client = ws.getClientandUpgradeConnection();
            new Thread(new Chess(ws, client)).start(); // need to clean up thread
        }
    }
}
