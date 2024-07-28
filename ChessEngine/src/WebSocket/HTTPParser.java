package websocket;

import java.util.Map;
import java.util.HashMap;
import java.nio.ByteBuffer;

/**
 * Parse/pack the components of an HTTP request/response.
 */
class HTTPParser {
    private static String LINE_SEP = "\r\n";

    public static Map<String, String> parseRequestStartLine(String req) {
        /* What we're after: GET / HTTP/1.1 */
        String[] requestStartLine = req.split(HTTPParser.LINE_SEP)[0].split(" ");

        Map<String, String> requestLineComponents = new HashMap();
        requestLineComponents.put("method", requestStartLine[0]);
        requestLineComponents.put("resource", requestStartLine[1]);
        requestLineComponents.put("version", requestStartLine[2]);

        return requestLineComponents;
    }

    public static Map<String, String> parseRequestHeaders(String req) {
        int i = 0;
        Map<String, String> requestHeaders = new HashMap();
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

    public static int packHTTPResponse(String[] lines, int nlines, byte[] buf) {
        ByteBuffer bbuf = ByteBuffer.wrap(buf);
        int totalBytes = 0;
        for (int i = 0; i < nlines; i++) {
            byte[] lineWithSuffix = (lines[i] + HTTPParser.LINE_SEP).getBytes();
            bbuf.put(lineWithSuffix);
            totalBytes += lineWithSuffix.length;
        }

        // Need extra "\r\n" to separate headers from body.
        byte[] suffix = HTTPParser.LINE_SEP.getBytes();
        bbuf.put(suffix);
        totalBytes += suffix.length;

        return totalBytes;
    }
}
