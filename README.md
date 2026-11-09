# Credo Bank API Tests

API tests for `GET /users` using Java 21, Maven, TestNG, REST Assured, Lombok, Jackson, and SQLite. WireMock 2.35.0 runs locally in Docker.

## Requirements

- JDK 21
- Maven 3.9+ or IntelliJ's bundled Maven
- Docker with Compose and a running Docker engine

## Run

From the project root:

```bash
docker compose up -d
mvn clean test
```

The suite runs nine cases across six test methods: all users, age and gender filters, invalid age, invalid gender, and a server error.

To stop WireMock:

```bash
docker compose down
```

In IntelliJ, the same suite can be run using the Maven `test` goal.

## Results

Test reports are saved in `target/surefire-reports/`. Database results are saved in `target/test-results.sqlite`, in the `test_results` table:

```sql
SELECT id, test_name, status, execution_time
FROM test_results
ORDER BY test_name;
```

Each method has one record. If any DataProvider case fails, the method is recorded as `FAILED`. Later runs update existing records by method name.

Run `mvn test` again without `clean` to check updates. `mvn clean` removes the database along with the rest of `target/`.

## Mock behavior

Normal and invalid-input stubs are registered by the inherited `@BeforeClass` method in `BaseTest`. The server-error test adds a temporary higher-priority stub and removes it afterward. Tests run sequentially because they share WireMock mappings.

Invalid age returns 400. Invalid gender returns 422, one of the outcomes allowed by the assignment. Error messages are defined by the mocks. The tests validate configured API responses, not a real backend's filtering logic.

## Structure

- `src/main/java/ge/credo`: services, models, DataProviders, configuration, WireMock setup, and database reporting.
- `src/test/java/ge/credo/base`: shared test setup.
- `src/test/java/ge/credo/tests`: API tests.
- `testng.xml`: test suite configuration.

## Configuration

Defaults:

- API: `http://localhost:8080`
- Database: `target/test-results.sqlite`

Both can be overridden:

```bash
mvn test "-Dapi.baseUrl=http://localhost:8081" "-Dresults.db=target/other.sqlite"
```

When changing the API port, update the host port in `docker-compose.yml` as well.
