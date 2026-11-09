package ge.credo.mocks;

import ge.credo.config.TestConfig;
import ge.credo.utils.JsonSupport;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;


public final class WireMockManager {
    private WireMockManager() { }

    public static void verifyRunning() {
        try {
            Response response = given().baseUri(TestConfig.apiBaseUrl())
                    .get("/__admin/mappings");
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Unexpected WireMock admin status: "
                        + response.statusCode());
            }
        } catch (Exception e) {
            throw new IllegalStateException("WireMock is not running at "
                    + TestConfig.apiBaseUrl()
                    + ". Start it with: docker compose up -d", e);
        }
    }

    public static void resetMappings() {
        given().baseUri(TestConfig.apiBaseUrl())
                .delete("/__admin/mappings")
                .then().statusCode(anyOf(is(200), is(204)));
    }

    public static void stubUsers(Map<String, String> query, int status, Object payload) {
        registerStub(query, status, payload, 5);
    }

    public static String stubServerError() {
        return registerStub(Map.of(), 500, Map.of("error", "Internal server error"), 1);
    }

    public static void removeStub(String id) {
        given().baseUri(TestConfig.apiBaseUrl())
                .delete("/__admin/mappings/" + id)
                .then().statusCode(anyOf(is(200), is(204)));
    }

    private static String registerStub(Map<String, String> query, int status,
                                       Object payload, int priority) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("method", "GET");
        request.put("urlPath", "/users");

        // A normal unfiltered request must not accidentally match a filtered one.
        Map<String, Object> queryMatchers = new LinkedHashMap<>();
        queryMatchers.put("age", Map.of("absent", true));
        queryMatchers.put("gender", Map.of("absent", true));
        query.forEach((key, value) ->
                queryMatchers.put(key, Map.of("equalTo", value)));
        request.put("queryParameters", queryMatchers);

        Map<String, Object> mockResponse = Map.of(
                "status", status,
                "headers", Map.of("Content-Type", "application/json"),
                "body", JsonSupport.toJson(payload)
        );
        String id = UUID.randomUUID().toString();
        Map<String, Object> mapping = Map.of(
                "id", id,
                "priority", priority,
                "request", request,
                "response", mockResponse
        );
        given().baseUri(TestConfig.apiBaseUrl())
                .contentType(ContentType.JSON)
                .body(JsonSupport.toJson(mapping))
                .post("/__admin/mappings")
                .then().statusCode(anyOf(is(200), is(201)));
        return id;
    }
}
