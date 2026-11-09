package ge.credo.services;

import ge.credo.config.TestConfig;
import io.restassured.response.Response;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserService extends BaseService {

    public Response getUsers(Map<String, ?> filters) {
        return given()
                .spec(getRequestSpec(TestConfig.apiBaseUrl()))
                .queryParams(filters)
                .when()
                .get("/users");
    }
}
