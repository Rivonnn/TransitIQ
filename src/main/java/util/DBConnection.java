package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Single place that knows how to reach the Postgres database. DAOs call getConnection() and never
 * see the URL or credentials themselves. This is a utility class with only static members, so its
 * constructor is private, and it can't be instantiated.
 */
public class DBConnection {

  // Local Postgres defaults: host, port and database created from schema.sql
  private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/transitiq";
  private static final String DEFAULT_USER = "postgres";
  private static final String URL_ENV_VAR = "TRANSITIQ_DB_URL";
  private static final String USER_ENV_VAR = "TRANSITIQ_DB_USER";
  private static final String PASSWORD_ENV_VAR = "TRANSITIQ_DB_PASSWORD";

  private DBConnection() {}

  /**
   * Opens a new connection to the database. The caller owns it and must close it, which is why the
   * DAOs call this inside try-with-resources.
   *
   * @return a fresh connection
   * @throws SQLException if the database is unreachable, the credentials are wrong, or the JDBC
   *     driver is missing from the classpath
   */
  public static Connection getConnection() throws SQLException {
    String url = getEnvOrDefault(URL_ENV_VAR, DEFAULT_URL);
    String user = getEnvOrDefault(USER_ENV_VAR, DEFAULT_USER);
    String password = getEnvOrDefault(PASSWORD_ENV_VAR, "");

    return DriverManager.getConnection(url, user, password);
  }

  private static String getEnvOrDefault(String envVarName, String defaultValue) {
    String value = System.getenv(envVarName);
    if (value == null || value.isBlank()) {
      return defaultValue;
    }

    return value;
  }
}
