package exceptions;

import java.io.Serial;

/** Thrown when a typed station name does not match any station. */
public class InvalidStationException extends Exception {
  // Required for Serializable - value can be arbitrary since we don't use serialization
  @Serial private static final long serialVersionUID = 1L;

  public InvalidStationException(String message) {
    super(message);
  }
}
