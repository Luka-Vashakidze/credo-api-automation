package ge.credo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ge.credo.data.models.User;
import java.util.List;

public final class JsonSupport {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonSupport() { }

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize mock response", e);
        }
    }

    public static List<User> toUsers(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<List<User>>() { });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not deserialize APi response: " + json, e);
        }
    }
}
