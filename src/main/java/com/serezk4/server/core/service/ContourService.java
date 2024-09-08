package com.serezk4.server.core.service;

import com.serezk4.server.core.dto.CoordinatesDto;

/**
 * Interface for checking if the point is inside the contour.
 *
 * @see ContourServiceImpl
 * @see CoordinatesDto
 * @since 1.0
 */
public interface ContourService {
    /**
     * Checks if the point is inside the contour.
     *
     * @param coordinates coordinates to check
     * @return true if the point is inside the contour, false otherwise
     */
    boolean isInsideContour(CoordinatesDto coordinates);
}
