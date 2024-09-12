package com.serezk4.server.fcgi.util;

import com.fastcgi.FCGIInterface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Utility class for FastCGI.
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
public class FcgiUtil {
    /**
     * Reads the request body from the FastCGI input stream.
     *
     * @return The request body as a string.
     * @throws IOException If an I/O error occurs.
     */
    public static String readRequestBody() throws IOException {
        FCGIInterface.request.inStream.fill();

        var contentLength = FCGIInterface.request.inStream.available();
        var buffer = ByteBuffer.allocate(contentLength);
        var readBytes = FCGIInterface.request.inStream.read(buffer.array(), 0, contentLength);

        var requestBodyRaw = new byte[readBytes];
        buffer.get(requestBodyRaw);
        buffer.clear();

        return new String(requestBodyRaw, StandardCharsets.UTF_8);
    }

    /**
     * Reads the request parameters from the FastCGI request.
     *
     * @return The request parameters.
     */
    public static Properties readRequestParams() {
        final String paramsString = FCGIInterface.request.params.getProperty("REQUEST_URI", "?").split("\\?")[1];
        return paramsString.isBlank() ? new Properties() : parseParams(paramsString);
    }

    /**
     * Parses the parameters string.
     *
     * @param paramsString The parameters string.
     * @return The parameters.
     */
    private static Properties parseParams(String paramsString) {
        StringTokenizer tokenizer = new StringTokenizer(paramsString, "&");
        Properties params = new Properties();
        while (tokenizer.hasMoreTokens()) {
            String[] pair = tokenizer.nextToken().split("=");
            params.setProperty(pair[0], pair[1]);
        }
        return params;
    }
}
