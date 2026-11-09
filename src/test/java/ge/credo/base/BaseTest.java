package ge.credo.base;

import ge.credo.data.models.User;
import ge.credo.mocks.WireMockManager;
import java.util.List;
import java.util.Map;
import org.testng.annotations.BeforeClass;

public abstract class BaseTest {
    private static final User ALICE = User.builder()
            .id(1).name("Alice").age(30).gender("female").build();
    private static final User BOB = User.builder()
            .id(2).name("Bob").age(25).gender("male").build();

    @BeforeClass
    public void prepareWireMock() {
        WireMockManager.verifyRunning();
        WireMockManager.resetMappings();
        WireMockManager.stubUsers(Map.of(), 200, List.of(ALICE, BOB));
        WireMockManager.stubUsers(Map.of("age", "30"), 200, List.of(ALICE));
        WireMockManager.stubUsers(Map.of("age", "25"), 200, List.of(BOB));
        WireMockManager.stubUsers(Map.of("age", "99"), 200, List.of());
        WireMockManager.stubUsers(Map.of("gender", "female"), 200, List.of(ALICE));
        WireMockManager.stubUsers(Map.of("gender", "male"), 200, List.of(BOB));
        WireMockManager.stubUsers(Map.of("age", "-1"), 400, Map.of("error", "Invalid age"));
        WireMockManager.stubUsers(Map.of("gender", "unknown"), 422,
                Map.of("error", "Unsupported gender"));
    }
}
