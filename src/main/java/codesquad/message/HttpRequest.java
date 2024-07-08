package codesquad.message;

import java.util.Map;

public record HttpRequest(
        HttpStartLine httpStartLine,
        HttpHeaders httpHeaders,
        String body) {

    private static final String LINE_SEPARATOR = "\n";

    public static HttpRequest parse(String rawHttpMessage) {
        String[] headAndBody = rawHttpMessage.split(LINE_SEPARATOR + LINE_SEPARATOR);
        String httpHead = headAndBody[0];

        String[] splitHead = httpHead.split(LINE_SEPARATOR, 2);
        String rawStartLine = splitHead[0];
        HttpStartLine httpStartLine = HttpStartLine.parse(rawStartLine);

        String rawHttpHeaders = splitHead[1];
        HttpHeaders httpHeaders = HttpHeaders.parse(rawHttpHeaders);

        if(headAndBody.length >= 2) {
            String httpBody = headAndBody[1];

        }

        return new HttpRequest(httpStartLine, httpHeaders, "");
    }

    public String method() {
        return httpStartLine.method();
    }

    public String requestUrl() {
        return httpStartLine.path();
    }

    public String httpVersion() {
        return httpStartLine.version();
    }

    public Map<String, String> header() {
        return httpHeaders.headers();
    }

    public Map<String, String> queries() {
        return httpStartLine.queries();
    }
}
