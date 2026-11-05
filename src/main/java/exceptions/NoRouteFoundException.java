package exceptions;

import java.io.Serial;

/** Thrown when no route exists between two stations. */
public class NoRouteFoundException extends TransitException {
  @Serial private static final long serialVersionUID = 1L;

  private final String sourceName;
  private final String destinationName;

  public NoRouteFoundException(String sourceName, String destinationName) {
    super("NO_ROUTE", "No route found from " + sourceName + " to " + destinationName);
    this.sourceName = sourceName;
    this.destinationName = destinationName;
  }

  public String getSourceName() {
    return sourceName;
  }

  public String getDestinationName() {
    return destinationName;
  }
}