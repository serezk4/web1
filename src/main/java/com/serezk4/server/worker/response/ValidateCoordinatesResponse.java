package com.serezk4.server.worker.response;

public record ValidateCoordinatesResponse(
        float x,
        float y,
        float r,
        boolean result,
        long bench
){
}
