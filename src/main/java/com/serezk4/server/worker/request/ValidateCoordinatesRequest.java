package com.serezk4.server.worker.request;

public record ValidateCoordinatesRequest(
        float x,
        float y,
        float r
){
}
