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

    /*
      For ease of reference, the data frame format is supplied here.
      Consult for greater detail:
      https://datatracker.ietf.org/doc/html/rfc6455#section-5.2

      0                   1                   2                   3
      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-------+-+-------------+-------------------------------+
     |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
     |I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
     |N|V|V|V|       |S|             |   (if payload len==126/127)   |
     | |1|2|3|       |K|             |                               |
     +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
     |     Extended payload length continued, if payload len == 127  |
     + - - - - - - - - - - - - - - - +-------------------------------+
     |                               |Masking-key, if MASK set to 1  |
     +-------------------------------+-------------------------------+
     | Masking-key (continued)       |          Payload Data         |
     +-------------------------------- - - - - - - - - - - - - - - - +
     :                     Payload Data continued ...                :
     + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
     |                     Payload Data continued ...                |
     +---------------------------------------------------------------+

    */

    /**
     * Returns the application data in text form.
     * For now, this only returns the data present in text form, and only 125
     * bytes of it. This will eventually support all data types in the protocol.
     *
     * @param f a byte array containing a WebSocket data frame
     * @return a String representation of the frame's application data
     */
    private static String parseDataFrame(byte[] f) {
        /* See the frame format. */

        if ((byte)(f[0] & 0x0f) == Opcode.CONN_CLOSE.value)  {
            System.out.println("The client has closed the connection.");
            return null;
        }

        /*
         * We want all the bits except the leftmost. This bit is the MASK
         * bit, which should always be set to one if the frame is coming from
         * the client. Should be validating this in the future.
         */
        int payloadLength = f[1] & 0x7f;

        /*
         * Minimally, the masking key is two bytes offset from the start of the
         * frame.
         */
        int maskOffset = 2;

        /*
         * See: https://datatracker.ietf.org/doc/html/rfc6455#section-5.2
         * for parsing of payload length.
         */
        if (payloadLength <= 125) {
            System.out.println(String.format("Payload length: %d bytes", payloadLength));
        } else if (payloadLength == 126) {
            // adjust payloadLength. this is not currently handled
            maskOffset += 2; // 2 bytes follow */
            System.out.println("16 bits of payload length.");
        } else if (payloadLength == 127) {
            // adjust payloadLength. this is not currently handled
            maskOffset += 8; // eight bytes follow
            System.out.println("64 bits of payload length.");
        }

        /*
         * The client is required to mask the payload data. The masking and
         * unmasking operations are the same. See here:
         * https://datatracker.ietf.org/doc/html/rfc6455#section-5.3
         */
        int payloadOffset = maskOffset + 4;
        for (int i = 0; i < payloadLength; i++) {
            f[i+payloadOffset] = (byte) (f[i+payloadOffset] ^ f[(i % 4) + maskOffset]);
        }

        return new String(Arrays.copyOfRange(f, payloadOffset, payloadOffset + payloadLength));
    }

    /**
     * Fill in the fields of a data frame to transport the given message.
     *
     * @param mesg the string to embed in the data frame
     * @return a byte array containing a WebSocket data frame
     */
    private static byte[] populateDataFrame(String mesg) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        /*
         * Write the first byte, indicating that this data frame is final (no
         * fragmentation), has not negotiated extensions, and is sending text
         * data.
         */
        bs.write((byte) (0x80 + Opcode.TEXT.value));

        byte[] data = mesg.getBytes();
        int payloadLength = data.length;
        if (payloadLength <= 125) {
            bs.write((byte) payloadLength);
        } else if (payloadLength == 126) {
            // write in payloadLength. this is not currently handled
            ;
        } else if (payloadLength == 127) {
            // write in payloadLength. this is not currently handled
            ;
        }

        bs.write(data, 0, data.length);
        return bs.toByteArray();
    }

    private ServerSocket ss;

    public WebSocketServer(int port) throws IOException {
        this.ss = new ServerSocket(port);
    }

    public Socket getClientandUpgradeConnection() {
        Socket client = null;
        try {
            client = ss.accept();

            System.out.println(String.format("Client %s accepted; " +
                                             "upgrading connection to WebSocket.",
                                             client.getRemoteSocketAddress()));

            // This is blocking.
            byte[] mesg = new byte[MAX_REQRESP_BUF_SIZ];
            client.getInputStream().read(mesg);

            if (upgradeConnection(client,
                                  HTTPParser.parseRequestHeaders(new String(mesg, "ascii")))) {
                System.out.println("Connection successfully upgraded.");
            } else {
                System.out.println("Connection could not be upgraded.");
                return null;
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Connection could not be upgraded.");
            return null;
        }

        return client;
    }

    /**
     * Upgrade the HTTP connection to a Websocket.
     * See: <a href="https://datatracker.ietf.org/doc/html/rfc6455#section-4.2.2">
     * Section 4.2.2: Sending the Server's Opening Handshake
     * </a> in particular step 5.
     *
     * @param client     a socket that has already requested a WebSocket upgrade
     * @param reqHeaders a map of HTTP request headers to their values
     * @return a boolean indicating upgrade success
     */
    private boolean upgradeConnection(Socket client, Map<String, String> reqHeaders) {
        int numRespLines = 4;
        String[] respLines = new String[numRespLines];
        respLines[0] = "HTTP/1.1 101 Switching Protocols";
        respLines[1] = "Upgrade: websocket";
        respLines[2] = "Connection: Upgrade";
        respLines[3] = String.format(
          "Sec-WebSocket-Accept: %s",
          WebSocketServer.getAcceptHeader(reqHeaders.get("sec-websocket-key"))
        );

        byte[] resp = new byte[MAX_REQRESP_BUF_SIZ];
        int respBytes = HTTPParser.packHTTPResponse(respLines, numRespLines, resp);

        try {
            client.getOutputStream().write(resp, 0, respBytes);
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }

        return true;
    }

    /* The following methods all block... */

    public void sendMesg(Socket client, String mesg) {
        try {
            client.getOutputStream().write(populateDataFrame(mesg));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String readMesg(Socket client) {
        int bytes;
        byte[] mesg = new byte[MAX_REQRESP_BUF_SIZ];
        try {
            bytes = client.getInputStream().read(mesg);
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

    public void sendPing(Socket client) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bs.write((byte) (0x80 + Opcode.PING.value));
        bs.write((byte) 0x00); // no data
        try {
            client.getOutputStream().write(bs.toByteArray());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void sendClose(Socket client) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bs.write((byte) (0x80 + Opcode.CONN_CLOSE.value));
        bs.write((byte) 0x00); // no data
        try {
            client.getOutputStream().write(bs.toByteArray());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
