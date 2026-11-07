package exceptions;

import java.io.Serial;

/**
 * Thrown when a database operation fails, such as a connection error, a failed query, or a
 * duplicate train ID. It wraps the original SQLException so the real cause is still visible in the
 * stack trace, while the rest of the app only deals with TransitException types.
 */
public final class DataAccessException extends TransitException {
  @Serial private static final long serialVersionUID = 1L;

  /**
   * @param message a readable description, which the admin screen shows directly to the user
   * @param cause the underlying SQLException
   */
  public DataAccessException(String message, Throwable cause) {
    super("DATABASE_ERROR", message);
    initCause(cause);
  }
}
