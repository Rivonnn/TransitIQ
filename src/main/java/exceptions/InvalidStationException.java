package exceptions;

import java.io.Serial;

/** Thrown when a typed station name does not match any station. */
public class InvalidStationException extends TransitException {
  @Serial private static final long serialVersionUID = 1L;

  private final String enteredName;

  public InvalidStationException(String enteredName) {
    super("INVALID_STATION", "No station named '" + enteredName + "'.");
    this.enteredName = enteredName;
  }

  public String getEnteredName() {
    return enteredName;
  }
}