package codesquad.message;

import java.util.HashMap;
import java.util.Map;

public final class HttpResponse {
    private final String version;
    private final HttpStatusCode statusCode;
    private final Map<String, String> headers = new HashMap<>();
    private final String body;

    public HttpResponse(
            String version,
            HttpStatusCode statusCode,
            String body) {
        this.version = version;
        this.statusCode = statusCode;
        this.body = body;
    }

    public String toHttpMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(version).append(" ")
                .append(statusCode.getStatusCode()).append(" ")
                .append(statusCode.getStatusText()).append("\n");
        headers.forEach((key, value) -> sb.append(key).append(": ").append(value).append("\n"));
        sb.append("\n");
        sb.append(body);
        return sb.toString();
    }

    public void addHeader(String key, String contentType) {
        headers.put(key, contentType);
    }
}
