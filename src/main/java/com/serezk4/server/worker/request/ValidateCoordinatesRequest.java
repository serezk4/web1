package com.serezk4.server.worker.request;

/**
 * Validate coordinates request.
 * @param x - x coordinate
 * @param y - y coordinate
 * @param r - radius
 */
public record ValidateCoordinatesRequest(
        Double x,
        Double y,
        Double r
){
}
