package com.serezk4.server.fcgi.message.converter;

import com.serezk4.server.fcgi.exc.ValidationException;

import java.util.Properties;

/**
 * Converter between encoded and decoded values
 *
 * @param <RQ> request type
 * @param <RS> response type
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
public interface FcgiConverter<RQ, RS> {
    /**
     * Convert entity to request
     * @param params entity
     * @return request
     */
    RQ encode(Properties params);

    /**
     * Convert response to entity
     * @param response response
     * @return response entity
     */
    String decode(RS response);

    void validate(RQ request) throws ValidationException;
}
