package com.serezk4.server.fcgi.exc;

import java.util.Map;

/**
 * Validation exception.
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
public class ValidationException extends RuntimeException {
    /**
     * Base constructor.
     * @param errors errors
     */
    public ValidationException(Map<String, String> errors) {
        super(errors.toString());
    }

    /**
     * Base constructor.
     * @param message message
     */
    public ValidationException(String message) {
        super(message);
    }
}
