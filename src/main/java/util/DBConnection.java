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

  // Local Postgres: host, port and the database created from schema.sql
  private static final String URL = "jdbc:postgresql://localhost:5432/transitiq";
  private static final String USER = "postgres";
  private static final String PASSWORD = "postgres";

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
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}
