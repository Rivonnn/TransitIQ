package exceptions;

/**
 * Thrown when no route exists between two stations.
 */
public class NoRouteFoundException extends Exception {
    public NoRouteFoundException(String message) {
        super(message);
    }
}
