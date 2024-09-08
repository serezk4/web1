package com.serezk4.server.worker.response;

/**
 * Validate coordinates response.
 * @param x - x coordinate
 * @param y - y coordinate
 * @param r - r coordinate
 * @param result - result of validation
 * @param bench - benchmark
 */
public record ValidateCoordinatesResponse(
        Double x,
        Double y,
        Double r,
        boolean result,
        long bench
){
}
