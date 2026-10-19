package exceptions;

import java.io.Serial;

/** Thrown when no route exists between two stations. */
public class NoRouteFoundException extends Exception {
  // Required for Serializable - value can be arbitrary since we don't use serialization
  @Serial private static final long serialVersionUID = 1L;

  public NoRouteFoundException(String message) {
    super(message);
  }
}
