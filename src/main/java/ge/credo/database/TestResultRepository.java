package ge.credo.database;

import ge.credo.config.TestConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public final class TestResultRepository {
    private final String jdbcUrl;

    public TestResultRepository() {
        Path dbFile = Path.of(TestConfig.resultsDbPath()).toAbsolutePath();
        try {
            if (dbFile.getParent() != null) {
                Files.createDirectories(dbFile.getParent());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create SQLite directory", e);
        }
        this.jdbcUrl = "jdbc:sqlite:" + dbFile;
    }

    public void initialize() {
        String sql = """
                CREATE TABLE IF NOT EXISTS test_results (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  test_name TEXT NOT NULL UNIQUE,
                  status TEXT NOT NULL CHECK(status IN ('PASSED', 'FAILED')),
                  execution_time DATETIME NOT NULL
                )
                """;
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("SQLite schema initialization failed", e);
        }
    }

    public void saveResult(String methodName, String status) {
        String sql = """
                INSERT INTO test_results(test_name, status, execution_time)
                VALUES (?, ?, datetime('now', 'localtime'))
                ON CONFLICT(test_name) DO UPDATE SET
                    status = excluded.status,
                    execution_time = excluded.execution_time
                """;
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, methodName);
            statement.setString(2, status);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot persist test result for " + methodName, e);
        }
    }
}
