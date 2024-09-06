package com.serezk4.server.worker;

import com.serezk4.server.fcgi.exc.ValidationException;
import com.serezk4.server.fcgi.worker.FcgiWorker;
import com.serezk4.server.worker.request.ValidateCoordinatesRequest;
import com.serezk4.server.worker.response.ValidateCoordinatesResponse;

import java.util.Properties;
import java.util.stream.Stream;

/**
 * A {@link FcgiWorker} coordinate validation worker.
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
public final class TestWorker extends FcgiWorker<ValidateCoordinatesRequest, ValidateCoordinatesResponse> {

    /**
     * Processes the request.
     * @param request The request body.
     * @return The response body.
     */
    @Override
    protected ValidateCoordinatesResponse process(ValidateCoordinatesRequest request) {
        return new ValidateCoordinatesResponse(
                request.x(),
                request.y(),
                request.r(),
                request.x() >= 0 && request.y() >= 0 && request.x() <= request.r() && request.y() <= request.r() / 2,
                0
        );
    }

    /**
     * Convert string to request entity
     * @param params request
     * @return request entity {@link ValidateCoordinatesRequest}
     */
    @Override
    public ValidateCoordinatesRequest encode(Properties params) {
        if (!Stream.of("x", "y", "r").allMatch(var -> params.containsKey(var) && params.getProperty(var).matches("^-?\\d+(\\.\\d+)?$")))
            throw new IllegalArgumentException("Not all required parameters are present or they are not numbers.");

        return new ValidateCoordinatesRequest(
                Float.parseFloat(params.getProperty("x")),
                Float.parseFloat(params.getProperty("y")),
                Float.parseFloat(params.getProperty("r"))
        );
    }

    /**
     * Convert response entity to string
     * @param response response
     * @return response entity {@link ValidateCoordinatesResponse}
     */
    @Override
    public String decode(ValidateCoordinatesResponse response) {
        return """
                "x": %.2f,
                "y": %.2f,
                "r": %.2f,
                "result": %b,
                "bench": %d
                """.formatted(response.x(), response.y(), response.r(), response.result(), response.bench());
    }

    /**
     * Validate request
     * @param request request
     * @throws ValidationException if request is invalid
     */
    @Override
    public void validate(ValidateCoordinatesRequest request) throws ValidationException {
        if (request.x() < -5 || request.x() > 5 || request.y() < -5 || request.y() > 5 || request.r() < 0 || request.r() > 5)
            throw new ValidationException("Invalid parameters.");
    }
}
