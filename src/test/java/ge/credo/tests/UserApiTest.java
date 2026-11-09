package ge.credo.tests;

import ge.credo.base.BaseTest;
import ge.credo.services.UserService;
import ge.credo.data.dataprovider.UserDataProvider;
import ge.credo.mocks.WireMockManager;
import ge.credo.listeners.TestResultListener;
import ge.credo.data.models.User;
import ge.credo.utils.JsonSupport;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestResultListener.class)
public final class UserApiTest extends BaseTest {
    private final UserService userService = new UserService();

    @Test(dataProvider = "allUsers", dataProviderClass = UserDataProvider.class)
    public void testGetAllUsers_Positive(List<User> expected) {
        Response response = userService.getUsers(Map.of());
        assertUsers(response, expected);
    }

    @Test(dataProvider = "validAges", dataProviderClass = UserDataProvider.class)
    public void testFilterByAge_Positive(String age, List<User> expected) {
        Map<String, String> filter = Map.of("age", age);
        Response response = userService.getUsers(filter);
        assertUsers(response, expected);
    }

    @Test(dataProvider = "validGenders", dataProviderClass = UserDataProvider.class)
    public void testFilterByGender_Positive(String gender, List<User> expected) {
        Map<String, String> filter = Map.of("gender", gender);
        Response response = userService.getUsers(filter);
        assertUsers(response, expected);
    }

    @Test(dataProvider = "invalidAges", dataProviderClass = UserDataProvider.class)
    public void testInvalidAge_Negative(String age, int expectedStatus, String expectedError) {
        Map<String, String> filter = Map.of("age", age);
        Response response = userService.getUsers(filter);
        Assert.assertEquals(response.statusCode(), expectedStatus);
        Assert.assertEquals(response.jsonPath().getString("error"), expectedError);
    }

    @Test(dataProvider = "serverFailures", dataProviderClass = UserDataProvider.class)
    public void testInternalServerError_Negative(int expectedStatus, String expectedError) {
        String stubId = WireMockManager.stubServerError();
        try {
            Response response = userService.getUsers(Map.of());
            Assert.assertEquals(response.statusCode(), expectedStatus);
            Assert.assertEquals(response.jsonPath().getString("error"), expectedError);
        } finally {
            WireMockManager.removeStub(stubId);
        }
    }

    @Test(dataProvider = "invalidGenders", dataProviderClass = UserDataProvider.class)
    public void testInvalidGender_Negative(String gender, int expectedStatus, String expectedError) {
        Map<String, String> filter = Map.of("gender", gender);
        Response response = userService.getUsers(filter);
        Assert.assertEquals(response.statusCode(), expectedStatus);
        Assert.assertEquals(response.jsonPath().getString("error"), expectedError);
    }

    private void assertUsers(Response response, List<User> expected) {
        Assert.assertEquals(response.statusCode(), 200, "Expected a successful GET /users");
        Assert.assertTrue(response.contentType().contains("application/json"),
                "Response should be JSON");
        List<User> actual = JsonSupport.toUsers(response.asString());
        Assert.assertEquals(actual, expected,
                "Response must contain the expected users with correct fields");
    }
}
