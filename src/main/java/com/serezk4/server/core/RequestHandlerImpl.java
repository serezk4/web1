package com.serezk4.server.core;

import com.fastcgi.FCGIInterface;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.serezk4.server.core.dto.CoordinatesDto;
import com.serezk4.server.core.service.ContourService;
import com.serezk4.server.core.validation.ValidationService;
import com.serezk4.server.fcgi.FcgiInterfaceHolder;
import com.serezk4.server.json.ObjectMapperHolder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of the {@link RequestHandler} interface.
 * Handles requests from the FastCGI interface.
 *
 * @see RequestHandler
 * @see FCGIInterface
 * @see FcgiInterfaceHolder
 * @since 1.0
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestHandlerImpl implements RequestHandler {
    ContourService contourService;
    ValidationService validationService;

    public RequestHandlerImpl(ContourService contourService, ValidationService validationService) {
        this.contourService = contourService;
        this.validationService = validationService;
    }

    @Override
    public void handle() {
        var fcgiInterface = FcgiInterfaceHolder.getInstance();

        while (fcgiInterface.FCGIaccept() >= 0) {
            try {
                String content = "<td>%s</td>";

                String requestBody = readRequestBody();

                CoordinatesDto coordinates = ObjectMapperHolder
                        .getInstance().readValue(requestBody, CoordinatesDto.class);

                if (!validationService.validate(coordinates)) {
                    error("Request data is invalid");
                    continue;
                }

                String result = contourService.isInsideContour(coordinates) ? "true" : "false";
                content = content.formatted(result);

                String response = """
                        HTTP/2 200 OK
                        Content-Type: text/html
                        Content-Length: %d
                                            
                        %s
                                            
                        """.formatted(content.getBytes(StandardCharsets.UTF_8).length, content);

                System.out.println(response);
            } catch (Exception e) {
                error("Can't process request");
            }
        }
    }


    @Override
    public void error(String message) {
        message = message.replace("\n", " ");

        String content = """
                <td>%s</td>
                """.formatted(message);


        var response = """
                HTTP/2 400 Bad Request
                Content-Type: text/html
                Content-Length: %d
                                    
                %s
                                    
                """.formatted(content.getBytes(StandardCharsets.UTF_8).length, content);

        System.out.println(response);
    }

    private String readRequestBody() throws IOException {
        FCGIInterface.request.inStream.fill();

        var contentLength = FCGIInterface.request.inStream.available();
        var buffer = ByteBuffer.allocate(contentLength);
        var readBytes = FCGIInterface.request.inStream.read(buffer.array(), 0, contentLength);

        var requestBodyRaw = new byte[readBytes];
        buffer.get(requestBodyRaw);
        buffer.clear();

        return new String(requestBodyRaw, StandardCharsets.UTF_8);
    }
}
