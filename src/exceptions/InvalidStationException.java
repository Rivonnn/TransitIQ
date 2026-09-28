package exceptions;

/**
 * Thrown when a typed station name does not match any station.
 */
public class InvalidStationException extends Exception {
    public InvalidStationException(String message) {
        super(message);
    }
}
