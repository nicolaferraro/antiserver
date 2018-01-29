package io.antiserver.api;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Serializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public byte[] serialize(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T deserialize(InputStream data, Class<T> tClass) {
        try {
            return OBJECT_MAPPER.readValue(data, tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T deserialize(byte[] data, Class<T> tClass) {
        try {
            return OBJECT_MAPPER.readValue(data, tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
