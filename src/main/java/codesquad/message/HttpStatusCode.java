package codesquad.message;

public enum HttpStatusCode {
    OK(200, "OK"), FOUND(302, "Found");

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
