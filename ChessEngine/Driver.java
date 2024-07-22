import java.io.IOException;
import WebSocketServer.WebSocketServer;


public class Driver {
    public static void main(String[] args) {
        WebSocketServer ss = new WebSocketServer(7897);
        ss.getClientandUpgradeConnection();

        while (true) {
            String mesg = ss.readMesg();

            System.out.println(mesg);
            /* rememeber, split takes a regex, so we need to
               escape the alternation operator */
            String[] mesgd = mesg.split("\\|");
            for (String p : mesgd) {
                System.out.println(p);
            }

            String[] resp = new String[3];
            resp[0] = mesgd[0];
            resp[1] = mesgd[1];
            resp[2] = mesgd[2];
            ss.sendMesg(String.join("|", resp));
        }
    }
}
