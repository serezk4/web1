package com.serezk4.server.worker;

import com.serezk4.server.fcgi.exc.ValidationException;
import com.serezk4.server.fcgi.worker.FcgiWorker;
import com.serezk4.server.worker.request.ValidateCoordinatesRequest;
import com.serezk4.server.worker.response.ValidateCoordinatesResponse;
import com.serezk4.server.worker.util.CoordinatesChecker;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A {@link FcgiWorker} coordinate validation worker.
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
public final class CoordinatesValidationWorker extends FcgiWorker<ValidateCoordinatesRequest, ValidateCoordinatesResponse> {

    /**
     * Processes the request.
     *
     * @param request The request body.
     * @return The response body.
     */
    @Override
    protected ValidateCoordinatesResponse process(ValidateCoordinatesRequest request) {
        final long start = System.nanoTime();
        return new ValidateCoordinatesResponse(
                request.x(), request.y(), request.r(),
                CoordinatesChecker.check(request),
                System.nanoTime() - start
        );
    }

    /**
     * Convert string to request entity
     *
     * @param params request
     * @return request entity {@link ValidateCoordinatesRequest}
     */
    @Override
    public ValidateCoordinatesRequest encode(Properties params) {
        if (!Stream.of("x", "y", "r").allMatch(var -> params.containsKey(var) && params.getProperty(var).matches("^-?\\d+(\\.\\d+)?$")))
            throw new IllegalArgumentException("Not all required parameters are present or they are not numbers." + params.toString());

        return new ValidateCoordinatesRequest(
                Double.parseDouble(params.getProperty("x")),
                Double.parseDouble(params.getProperty("y")),
                Double.parseDouble(params.getProperty("r"))
        );
    }

    /**
     * Convert response entity to string
     *
     * @param response response
     * @return response entity {@link ValidateCoordinatesResponse}
     */
    @Override
    public String decode(ValidateCoordinatesResponse response) {
        return """
                <tr>
                <td>%.1f</td>
                <td>%.1f</td>
                <td>%.1f</td>
                <td>%b</td>
                <td>%d</td>
                </tr>
                """.formatted(response.x(), response.y(), response.r(), response.result(), response.bench());
    }

    /**
     * Validate request
     *
     * @param request request
     * @throws ValidationException if request is invalid
     */
    @Override
    public void validate(ValidateCoordinatesRequest request) throws ValidationException {
        validateX(request.x());
        validateY(request.y());
        validateR(request.r());
    }

    /**
     * Validate x
     *
     * @param x x
     * @throws ValidationException if x is invalid
     */
    private void validateX(Double x) throws ValidationException {
        if (!Set.of(-4d, -3d, -2d, -1d, 0d, 1d, 2d, 3d, 4d).contains(x)) throw new ValidationException("X is not in the valid range.");
    }

    /**
     * Validate y
     *
     * @param y y
     * @throws ValidationException if y is invalid
     */
    private void validateY(Double y) throws ValidationException {
        if (y < -5 || y > 3) throw new ValidationException("Y is not in the valid range.");
    }

    /**
     * Validate r
     *
     * @param r r
     * @throws ValidationException if r is invalid
     */
    private void validateR(Double r) throws ValidationException {
        if (!Set.of(1d, 1.5d, 2d, 2.5d, 3d).contains(r)) throw new ValidationException("R is not in the valid range.");
    }
}
