package ge.credo.config;

public final class TestConfig {
    private TestConfig() { }

    public static String apiBaseUrl() {
        return System.getProperty("api.baseUrl", "http://localhost:8080")
                .replaceAll("/+$", "");
    }

    public static String resultsDbPath() {
        return System.getProperty("results.db", "target/test-results.sqlite");
    }
}
