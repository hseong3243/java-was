package codesquad.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class HttpResponse {
    private final String version;
    private final int statusCode;
    private final String statusText;
    private final Map<String, String> headers = new HashMap<>();
    private final String body;

    public HttpResponse(
            String version,
            int statusCode,
            String statusText,
            String body) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.body = body;
    }

    public String toHttpMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(version).append(" ").append(statusCode).append(" ").append(statusText).append("\n");
        headers.forEach((key, value) -> sb.append(key).append(": ").append(value).append("\n"));
        sb.append("\n");
        sb.append(body);
        return sb.toString();
    }

    public void addHeader(String key, String contentType) {
        headers.put(key, contentType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (HttpResponse) obj;
        return Objects.equals(this.version, that.version) &&
                this.statusCode == that.statusCode &&
                Objects.equals(this.statusText, that.statusText) &&
                Objects.equals(this.headers, that.headers) &&
                Objects.equals(this.body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, statusCode, statusText, headers, body);
    }

    @Override
    public String toString() {
        return "HttpResponse[" +
                "version=" + version + ", " +
                "statusCode=" + statusCode + ", " +
                "statusText=" + statusText + ", " +
                "headers=" + headers + ", " +
                "body=" + body + ']';
    }

}
