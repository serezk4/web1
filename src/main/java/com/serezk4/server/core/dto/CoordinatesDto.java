package com.serezk4.server.core.dto;

/**
 * Data transfer object for coordinates.
 * Interacts directly with client-side.
 *
 * @param x x-coordinate
 * @param y y-coordinate
 * @param r radius
 * @since 1.0
 */
public record CoordinatesDto(
        double x,
        double y,
        double r
) {
}
