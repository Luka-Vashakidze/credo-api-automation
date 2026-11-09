package ge.credo.data.dataprovider;

import ge.credo.data.models.User;
import java.util.List;
import org.testng.annotations.DataProvider;

public final class UserDataProvider {
    private static final User ALICE = User.builder()
            .id(1).name("Alice").age(30).gender("female").build();
    private static final User BOB = User.builder()
            .id(2).name("Bob").age(25).gender("male").build();

    @DataProvider(name = "allUsers")
    public static Object[][] allUsers() {
        return new Object[][] {{List.of(ALICE, BOB)}};
    }

    @DataProvider(name = "validAges")
    public static Object[][] validAges() {
        return new Object[][] {
                {"30", List.of(ALICE)},
                {"25", List.of(BOB)},
                {"99", List.of()}
        };
    }

    @DataProvider(name = "validGenders")
    public static Object[][] validGenders() {
        return new Object[][] {
                {"female", List.of(ALICE)},
                {"male", List.of(BOB)}
        };
    }

    @DataProvider(name = "invalidAges")
    public static Object[][] invalidAges() {
        return new Object[][] {{"-1", 400, "Invalid age"}};
    }

    @DataProvider(name = "serverFailures")
    public static Object[][] serverFailures() {
        return new Object[][] {{500, "Internal server error"}};
    }

    @DataProvider(name = "invalidGenders")
    public static Object[][] invalidGenders() {
        return new Object[][] {{"unknown", 422, "Unsupported gender"}};
    }

}
