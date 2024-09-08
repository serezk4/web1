package com.serezk4.server.core;

/**
 * Interface for handling requests.
 *
 * @see RequestHandlerImpl
 * @since 1.0
 */
public interface RequestHandler {
    /**
     * Handles requests.
     * If the request is valid, sends the result with 200 OK.
     * Otherwise, sends an error message.
     *
     * @see #error(String)
     */
    void handle();

    /**
     * Wrapper for sending error messages.
     * Hardcoded for 400 Bad Request.
     *
     * @param message error message
     */
    void error(String message);
}
