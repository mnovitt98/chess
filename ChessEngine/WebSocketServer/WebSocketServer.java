package WebSocketServer;

import java.util.Arrays;
import java.util.HashMap;
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


public class WebSocketServer {

    private static final int MAX_REQRESP_BUF_SIZ = 1024;

    private ServerSocket ss;
    private Socket client;
    public InputStream is;
    private OutputStream os;

    /* parsing request, no real error handling done here in the interest of
       time */

    private static HashMap<String, String> parseRequestStartLine(String req) {
        /* What we're after: GET / HTTP/1.1 */
        String[] requestLine = req.split("\r\n")[0].split(" ");

        HashMap<String, String> requestLineInfo = new HashMap();
        requestLineInfo.put("method", requestLine[0]);
        requestLineInfo.put("resource", requestLine[1]);
        requestLineInfo.put("version", requestLine[2]);

        return requestLineInfo;
    }

    private static HashMap<String, String> parseRequestHeaders(String req) {
        int i = 0;
        HashMap<String, String> requestHeaders = new HashMap();
        for (String line : req.split("\r\n")) {
            if (i++ == 0) { /* dont need the request start line */
                continue;
            }
            String[] kv = line.split(": ");
            if (kv.length == 1) { /* i.e. there was no match, happens at the */
                break;            /* end of the list of headers...           */
            }
            requestHeaders.put(kv[0].toLowerCase(), kv[1]);
        }
        return requestHeaders;
    }

    /* some helper methods */

    private static int convertByte(byte b) {
        /* java doesnt have unsigned types, truly despicable */
        if ((int) b < 0) {
            return b + 128; /* 10000000 */
        }
        return b;
    }

    /* put rfc link here */
    private static String getAcceptHeader(String key) {
        /* note random constant */
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes());
            String accept = Base64.getEncoder().encodeToString(md.digest());
            System.out.println(String.format("Key is : %s, Accept is: %s", key, accept));
            return accept;
        } catch (NoSuchAlgorithmException e) {
            ;
        }
        return "";
    }

    private static int packResponse(String[] lines, int nlines, byte[] buf) {
        int nBytesSuffix = "\r\n".getBytes().length;

        int totalBytes = 0;
        ByteBuffer bbuf = ByteBuffer.wrap(buf);
        for (int i = 0; i < nlines; i++) {
            int nBytesLine = lines[i].getBytes().length;
            bbuf.put((lines[i] + "\r\n").getBytes());
            totalBytes += nBytesLine + nBytesSuffix;
        }
        bbuf.put("\r\n".getBytes());
        totalBytes += nBytesSuffix;

        return totalBytes;
    }

    private static String parseDataFrame(byte[] f) {
        int payloadLength = convertByte(f[1]);
        int maskOffset = 2;
        if (payloadLength <= 125) {
            System.out.println(String.format("Only %d bytes", payloadLength));
        } else if (payloadLength == 126) {
            /* adjust payloadLength */
            maskOffset += 2; /* 2 bytes follow */
            System.out.println("Larger payload.");
        } else if (payloadLength == 127) {
            /* adjust payloadLength */
            maskOffset += 8; /* three bytes follow */
        }
        int payloadOffset = maskOffset + 4;
        for (int i = 0; i < payloadLength; i++) {
            f[i+payloadOffset] = (byte) (f[i+payloadOffset] ^ f[(i % 4) + maskOffset]);
        }

        return new String(Arrays.copyOfRange(f, payloadOffset, payloadOffset + payloadLength));
    }

    private static byte[] populateDataFrame(String mesg) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        /* first row */
        bs.write((byte) 0x81); /* sending text */

        int payloadLength = mesg.getBytes().length;
        if (payloadLength <= 125) {
            bs.write((byte) payloadLength);
        } else if (payloadLength == 126) {
            bs.write((byte) payloadLength);
        } else if (payloadLength == 127) {
            bs.write((byte) payloadLength);
        }

        bs.write(mesg.getBytes(), 0, mesg.getBytes().length);
        return bs.toByteArray();
    }


    public WebSocketServer(int port) {
        try {
            this.ss = new ServerSocket(port);
        } catch (Exception e) {
            ;
        }
    }

    public void getClientandUpgradeConnection() {
        try {
            this.client = ss.accept();
            this.is = client.getInputStream();
            this.os = client.getOutputStream();

            /* this is blocking. should check this request for validity. again
               in the interest of time, this will be defered. */
            byte[] mesg = new byte[MAX_REQRESP_BUF_SIZ];
            is.read(mesg);
            upgradeConnection(WebSocketServer.parseRequestHeaders(new String(mesg, "ascii")));

            System.out.println(WebSocketServer.parseRequestHeaders(new String(mesg, "ascii")));
            System.out.println(WebSocketServer.parseRequestHeaders(new String(mesg, "ascii")).get("sec-websocket-key"));

            System.out.println("Connection upgraded");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /* link rfc */
    private void upgradeConnection(HashMap<String, String> reqHeaders) {
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
        int respBytes = packResponse(respLines, numRespLines, resp);

        try {
            this.os.write(resp, 0, respBytes);
        } catch (IOException e) {
            System.out.println(e);;
        }
    }

    /* this will block */
    public void sendPing() {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bs.write((byte) 0x89);
        bs.write((byte) 0x00);
        try {
            this.os.write(bs.toByteArray());
        } catch (IOException e) {
            ;
        }
    }

    /* this will block */
    public void sendMesg(String mesg) {
        try {
            this.os.write(populateDataFrame(mesg));
        } catch (IOException e) {
            ;
        }
    }

    /* this will block */
    public String readMesg() {
        byte[] mesg = new byte[MAX_REQRESP_BUF_SIZ];
        int bytes;
        try {
            bytes = is.read(mesg);
            if (bytes < 0) {
                return "";
            }
        } catch (IOException e) {
            return "Exception";
        }

        System.out.println("We have more than zero bytes");
        return parseDataFrame(mesg);
    }
}
