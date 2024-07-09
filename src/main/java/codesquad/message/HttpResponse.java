package codesquad.message;

import java.util.HashMap;
import java.util.Map;

public final class HttpResponse {

    public static final String CRLF = "\n";

    private final String version;
    private final HttpStatusCode statusCode;
    private final Map<String, String> headers = new HashMap<>();
    private final byte[] body;

    public HttpResponse(
            String version,
            HttpStatusCode statusCode,
            byte[] body) {
        this.version = version;
        this.statusCode = statusCode;
        this.body = body;
    }

    public HttpResponse(
            String version,
            HttpStatusCode statusCode,
            String body) {
        this(version, statusCode, body.getBytes());
    }

    public byte[] getHttpMessageStartLine() {
        String sb = version + " " + statusCode.getStatusCode() + " " + statusCode.getStatusText() + CRLF;
        return sb.getBytes();
    }

    public byte[] getHttpMessageHeaders() {
        StringBuilder sb = new StringBuilder();
        headers.forEach((key, value) -> {
            sb.append(key).append(": ").append(value).append(CRLF);
        });
        return sb.toString().getBytes();
    }

    public byte[] getHttpMessageBody() {
        return body;
    }

    public void addHeader(String key, String contentType) {
        headers.put(key, contentType);
    }

    public void addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }
}
