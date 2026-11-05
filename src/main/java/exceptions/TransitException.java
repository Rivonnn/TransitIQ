package exceptions;

import java.io.Serial;

/** Base class for all TransitIQ errors. Carries a short code besides the message. */
public class TransitException extends Exception {
    @Serial private static final long serialVersionUID = 1L;

    private final String errorCode;

    public TransitException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}