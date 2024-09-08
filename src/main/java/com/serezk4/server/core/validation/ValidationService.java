package com.serezk4.server.core.validation;

import com.serezk4.server.core.dto.CoordinatesDto;

/**
 * Interface for validating coordinates. Used to check if the coordinates are valid.
 *
 * @see ValidationServiceImpl
 * @see CoordinatesDto
 * @since 1.0
 */
public interface ValidationService {
    /**
     * Validates the coordinates.
     *
     * @param coordinates coordinates to validate
     * @return true if the coordinates are valid, false otherwise
     */
    boolean validate(CoordinatesDto coordinates);
}
