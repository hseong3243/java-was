package codesquad.server.message;

public class RuntimeIOException extends RuntimeException{
    public RuntimeIOException(String message) {
        super(message);
    }

    public RuntimeIOException(Throwable cause) {
        super(cause);
    }

    public RuntimeIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
