package websocket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import websocket.HTTPParser;

/**
 * Establish and maintain a websocket connection with a client as defined by:
 * <a href="https://datatracker.ietf.org/doc/html/rfc6455">
 *   Request for Comments: 6455
 * </a>.
 * At time of writing, this class is not in full observance of the protocol; its
 * functionality goes as far as to establish a connection with a client, and to
 * send and receive data frames with small text payloads. Moreover, it limits
 * itself to dealing with one client at a time. These limitations will be
 * overcome, it is hoped, in due time.
 */
public class WebSocketServer {
    private static final int MAX_REQRESP_BUF_SIZ = 1024;
    private static final String ACCEPT_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    /**
     * Enumerate all possible opcodes for frame types. See:
     * <a href="">Section 5.2: Base Framing Protocol</a>
     */
    private static enum Opcode {
        CONTINUTATION ((byte) 0x0),
        TEXT          ((byte) 0x1),
        BINARY        ((byte) 0x2),
        // %x3-7 are reserved for further non-control frames
        CONN_CLOSE    ((byte) 0x8),
        PING          ((byte) 0x9),
        PONG          ((byte) 0xa);
        // %xB-F are reserved for further control frames

        private byte value;

        Opcode(byte val) {
            this.value = val;
        }
    }

    /**
     * Determine the unsigned value stored in the byte argument.
     * Since Java trades only in two's complement values, one must coerce a
     * negative byte value into an int to get at its unsigned contents. This
     * is done by taking the following sum:
     * <p>
     *   1111111111111111111111111xxxxxxx
     * + 00000000000000000000000010000000
     * ----------------------------------
     *   0000000000000000000000000xxxxxxx
     * </p>
     * The overflow is discarded of course. Again, please note this is only
     * performed when the byte is negative. Otherwise the value is the same
     * signed or unsigned. This function is necessary when working with bytes
     * off the wire.
     *
     * @param  b signed byte
     * @return a positive int
     */
    private static int convertByte(byte b) {
        // Here a widening primitive conversion is performed: b gets promoted to
        // the int value shown above before being added to 128.
        return b < 0 ? b + 128 : b;
    }

    /**
     * Produce the Sec-WebSocket-Accept header value as defined in:
     * <a href="https://datatracker.ietf.org/doc/html/rfc6455#section-4.2.2">
     *  Section 4.2.2: Sending the Server's Opening Handshake
     * </a>
     * <p></p>
     * Specifically, see step 5.4.
     *
     * @params key the Sec-WebSocket-Key header value sent by the client
     */
    private static String getAcceptHeader(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update((key + WebSocketServer.ACCEPT_GUID).getBytes());
            return Base64.getEncoder().encodeToString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return "";
    }

    private static String parseDataFrame(byte[] f) {
        int payloadLength = convertByte(f[1]);
        int maskOffset = 2;
        if (payloadLength <= 125) {
            System.out.println(String.format("Payload length: %d bytes", payloadLength));
        } else if (payloadLength == 126) {
            /* adjust payloadLength. this is not currently handled */
            maskOffset += 2; /* 2 bytes follow */
            System.out.println("Larger payload.");
        } else if (payloadLength == 127) {
            /* adjust payloadLength. this is not currently handled */
            maskOffset += 8; /* eight bytes follow */
            System.out.println("Larger payload.");
        }
        int payloadOffset = maskOffset + 4;
        for (int i = 0; i < payloadLength; i++) {
            f[i+payloadOffset] = (byte) (f[i+payloadOffset] ^ f[(i % 4) + maskOffset]);
        }

        return new String(Arrays.copyOfRange(f, payloadOffset, payloadOffset + payloadLength));
    }

    private static byte[] populateDataFrame(String mesg) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        bs.write((byte) 0x81); /* sending text */

        int payloadLength = mesg.getBytes().length;
        if (payloadLength <= 125) {
            bs.write((byte) payloadLength);
        } else if (payloadLength == 126) {
            /* write in payloadLength. this is not currently handled */
            ;
        } else if (payloadLength == 127) {
            /* write in payloadLength. this is not currently handled */
            ;
        }

        bs.write(mesg.getBytes(), 0, mesg.getBytes().length);
        return bs.toByteArray();
    }

    private ServerSocket ss;
    private Socket client;
    private InputStream is;
    private OutputStream os;

    public WebSocketServer(int port) throws IOException {
        this.ss = new ServerSocket(port);
    }

    public void getClientandUpgradeConnection() {
        try {
            this.client = ss.accept();
            this.is = client.getInputStream();
            this.os = client.getOutputStream();

            System.out.println(String.format("Client %s accepted; upgrading connection to websocket.",
                                             client.getRemoteSocketAddress()));

            // This is blocking.
            byte[] mesg = new byte[MAX_REQRESP_BUF_SIZ];
            is.read(mesg);

            upgradeConnection(HTTPParser.parseRequestHeaders(new String(mesg, "ascii")));
            System.out.println("Connection successfully upgraded.");
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Connection could not be upgraded.");
        }
    }

    /* link rfc */
    private void upgradeConnection(Map<String, String> reqHeaders) {
        int numRespLines = 4;
        String[] respLines = new String[numRespLines];
        respLines[0] = "HTTP/1.1 101 Switching Protocols";
        respLines[1] = "Upgrade: websocket";
        respLines[2] = "Connection: Upgrade";
        respLines[3] = String.format(
          "Sec-WebSocket-Accept: %s",
          getAcceptHeader(reqHeaders.get("sec-websocket-key"))
        );

        byte[] resp = new byte[MAX_REQRESP_BUF_SIZ];
        int respBytes = HTTPParser.packHTTPResponse(respLines, numRespLines, resp);

        try {
            this.os.write(resp, 0, respBytes);
        } catch (IOException e) {
            System.out.println(e);;
        }
    }

    /* The following methods all block... */
    public void sendPing() {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bs.write((byte) 0x89);
        bs.write((byte) 0x00);
        try {
            this.os.write(bs.toByteArray());
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    public void sendMesg(String mesg) {
        try {
            this.os.write(populateDataFrame(mesg));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String readMesg() {
        int bytes;
        byte[] mesg = new byte[MAX_REQRESP_BUF_SIZ];
        try {
            bytes = is.read(mesg);
            if (bytes > 0) {
                System.out.println("Processing client data frame.");
            } else if (bytes == 0) {
                System.out.println("Received an empty message.");
                return null;
            } else {
                System.out.println("Connection closed by client.");
                return null;
            }
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }

        return parseDataFrame(mesg);
    }
}
