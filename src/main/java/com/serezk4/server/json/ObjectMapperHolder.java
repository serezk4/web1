package com.serezk4.server.json;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperHolder {
    private static volatile ObjectMapper instance = null;

    public static ObjectMapper getInstance() {
        if (instance == null) {
            instance = new ObjectMapper();
        }
        return instance;
    }
}
