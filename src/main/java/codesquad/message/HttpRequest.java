package codesquad.message;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record HttpRequest(
        HttpStartLine httpStartLine,
        HttpHeaders httpHeaders,
        HttpCookies httpCookies,
        HttpBody httpBody) {

    private static final String LINE_SEPARATOR = "\n";
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);


    public static HttpRequest parse(String rawHttpMessage) {
        String[] headAndBody = rawHttpMessage.split(LINE_SEPARATOR + LINE_SEPARATOR);
        String httpHead = headAndBody[0];

        String[] splitHead = httpHead.split(LINE_SEPARATOR, 2);
        String rawStartLine = splitHead[0];
        HttpStartLine httpStartLine = HttpStartLine.parse(rawStartLine);

        String rawHttpHeaders = splitHead[1];
        HttpHeaders httpHeaders = HttpHeaders.parse(rawHttpHeaders);
        HttpCookies httpCookies = HttpCookies.parse(httpHeaders.get("Cookie"));

        if(headAndBody.length >= 2) {
            String rawHttpBody = headAndBody[1];
            HttpBody httpBody = HttpBody.parse(rawHttpBody);
            return new HttpRequest(httpStartLine, httpHeaders, httpCookies, httpBody);
        }

        return new HttpRequest(httpStartLine, httpHeaders, httpCookies, new HttpBody(new HashMap<>()));
    }

    public HttpMethod method() {
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

    public Map<String, String> bodyData() {
        return httpBody.data();
    }

    public Map<String, String> cookies() {
        return httpCookies.cookies();
    }
}
