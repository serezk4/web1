package com.serezk4.server.fcgi.worker;

import com.serezk4.server.fcgi.FcgiInterface;
import com.serezk4.server.fcgi.exc.ValidationException;
import com.serezk4.server.fcgi.message.converter.FcgiConverter;
import com.serezk4.server.fcgi.util.FcgiUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * A FastCGI worker.
 *
 * @param <RQ> The request type.
 * @param <RS> The response type.
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class FcgiWorker<RQ, RS> implements Runnable, FcgiConverter<RQ, RS> {
    /**
     * Main method.
     * In loop reads the request body and processes it.
     * Stops when the input stream is not ready.
     */
    @Override
    public void run() {
        try {
            while (FcgiInterface.getInstance().ready()) loop();
        } catch (IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }

    /**
     * Iterates the request.
     *
     * @throws IOException If an I/O error occurs.
     */
    private void loop() throws IOException {
        try {
            final RQ request = encode(FcgiUtil.readRequestParams()); validate(request);
            final RS response = process(request);
            final String decoded = decode(response);
            final String decodedWithHeaders = """
                    HTTP/2 200 OK
                    Content-Type: application/json
                    Content-Length: %d
                   
                    %s
                    
                    """.formatted(decoded.getBytes(StandardCharsets.UTF_8).length, decoded);

            System.out.println(decodedWithHeaders);
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Processes the request.
     *
     * @param request The request body.
     * @return The response body.
     */
    protected abstract RS process(RQ request);
}
