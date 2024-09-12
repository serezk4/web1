package com.serezk4.server.worker.util;

import com.serezk4.server.worker.request.ValidateCoordinatesRequest;

/**
 * Coordinates checker.
 *
 * @author serezk4
 * @version 1.0
 */
public class CoordinatesChecker {
    /**
     * Check coordinates.
     *
     * @param request request {@link ValidateCoordinatesRequest}
     * @return result of check
     */
    public static boolean check(ValidateCoordinatesRequest request) {
        return check(request.x(), request.y(), request.r());
    }

    /**
     * Check coordinates.
     * @param x x coordinate
     * @param y y coordinate
     * @param r r coordinate
     * @return result of check
     */
    public static boolean check(double x, double y, double r) {
        return (x >= 0 && y >= 0 && x * x + y * y <= r * r) ||
                (x >= -r / 2 && x <= 0 && y >= -r / 2 && y <= 0 && y >= -x) ||
                (x >= 0 && x <= r / 2 && y >= -r && y <= -r / 2);
    }
}
