package codesquad.server.message;

public enum HttpStatusCode {
    OK(200, "OK"), MOVED_PERMANENTLY(301, "Moved Permanently"), FOUND(302, "Found"), BAD_REQUEST(400,
            "Bad Request"), NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

    private final int statusCode;
    private final String statusText;

    HttpStatusCode(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }
}
