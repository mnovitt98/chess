import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.HashMap;
import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class WebSocket {
  public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
      byte[] a = new byte[]{(byte)Integer.parseInt("1000", 2)};
    System.out.print(a);
    ServerSocket server = new ServerSocket(7897);

    try {
      System.out.println("Server has started on 127.0.0.1:7897.\r\nWaiting for a connection…");
      Socket client = server.accept();
      System.out.println("A client connected.");

      InputStream is = client.getInputStream();
      byte[] mesg = new byte[1024];
      int mesg_size = is.read(mesg);

      String req = (new String(mesg, "ASCII"));
      HashMap<String, String> reqLine = WebSocket.parseRequestLine(req);
      HashMap<String, String> reqHeaders = WebSocket.parseRequestHeaders(req);
      System.out.println("incoming request");
      System.out.println(req);
      System.out.println();


      /* i will not be handling fragmentation... praying buffer size is sufficient */
      OutputStream os = client.getOutputStream();
      byte[] resp = new byte[1024];
      int wBytes = performHandshake(reqHeaders, resp);

      System.out.println("Sending out response handshake");
      System.out.println(new String(resp, "ASCII"));
      os.write(resp, 0, wBytes);

      System.out.println("Ping");
      System.out.println();
      os.write(sendPing());
      byte[] second = new byte[1024];
      mesg_size = is.read(second);
      System.out.println("Pong");
      System.out.println(String.format("%x", second[0]));
      System.out.println(new String(second, "ASCII"));

      os.write(populateFrame());
      byte[] third = new byte[1024];
      mesg_size = is.read(third);
      System.out.println("Final Follow up.");

      System.out.println(parseFrame(third));

      /* now into main loop for handling back and forth talk */
      while (true) {
          ;

      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

    private static final int FRAME_ROW_SIZE     = 4 * 8;
    private static final int FRAME_ROWS_MINIMUM = 8;

    private static int convertByte(byte b) {
        if ((int) b < 0) {
            return b + 128;
        }

        return b;
    }

    private static void dumpBytes(byte[] b, int length) {
        for (int i = 0; i < length; i++) {
            System.out.println(String.format("%x", b[i]));
        }
    }

    private static String parseFrame(byte[] f) throws UnsupportedEncodingException {
        int payloadLength = convertByte(f[1]);
        int maskOffset = 2;
        if (payloadLength <= 125) {
            ; /* no bytes follow */
            System.out.println(String.format("Only %d bytes", payloadLength));
        } else if (payloadLength == 126) {
            /* adjust payloadLength */
            maskOffset += 2; /* 2 bytes follow */
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

    private static byte[] populateFrame() {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        /* first row */
        bs.write((byte) 0x81); /* sending text */
        bs.write((byte) 0x0f);
        bs.write("This is a test.".getBytes(), 0, 15);

        return bs.toByteArray();
    }

    private static byte[] sendPing() {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bs.write((byte) 0x89);
        bs.write((byte) 0x00);
        return bs.toByteArray();

    }

    private static String getAcceptHeader(String key) throws UnsupportedEncodingException {
        try {
         MessageDigest md = MessageDigest.getInstance("SHA-1");
         md.update((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes());
         return Base64.getEncoder().encodeToString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            ;
        }
        return "";
    }

    private static int performHandshake(HashMap<String, String> reqHeaders, byte[] resp)
        throws UnsupportedEncodingException
    {
        String[] respLines = new String[10];
        respLines[0] = "HTTP/1.1 101 Switching Protocols";
        respLines[1] = "Upgrade: websocket";
        respLines[2] = "Connection: Upgrade";

        int writtenBytes = 0;
        respLines[3] = String.format(
          "Sec-WebSocket-Accept: %s",
          getAcceptHeader(reqHeaders.get("sec-websocket-key"))
        );

        return packResponse(respLines, 4, resp);
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


  /* parsing request */
  private static HashMap<String, String> parseRequestLine(String req) {
      String[] requestLine = req.split("\r\n")[0].split(" ");
      HashMap<String, String> requestLineInfo = new HashMap();
      requestLineInfo.put("method", requestLine[0]);
      requestLineInfo.put("resource", requestLine[1]);
      requestLineInfo.put("version", requestLine[2]);

      return requestLineInfo;
  }

  private static HashMap<String, String> parseRequestHeaders(String req) {
      String[] request = req.split("\r\n");
      HashMap<String, String> requestHeaders = new HashMap();
      int i = 0;
      for (String line : request) {
          if (i++ == 0) continue;
          String[] kv = line.split(": ");
          if (kv.length == 1) break;
          requestHeaders.put(kv[0], kv[1]);
      }
      return requestHeaders;
  }
}
