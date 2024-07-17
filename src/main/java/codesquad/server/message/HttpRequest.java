package codesquad.server.message;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record HttpRequest(
        HttpStartLine httpStartLine,
        HttpHeaders httpHeaders,
        HttpCookies httpCookies,
        HttpBody httpBody) {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    public static HttpRequest parse(InputStream clientInput) {
        try {
            return parseInner(clientInput);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private static HttpRequest parseInner(InputStream clientInput) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(clientInput));
        HttpStartLine httpStartLine = HttpStartLine.parse(br.readLine());
        HttpHeaders httpHeaders = HttpHeaders.parse(br);
        HttpCookies httpCookies = HttpCookies.parse(httpHeaders);

        HttpBody httpBody = HttpBody.parse(br, clientInput, httpHeaders);
        return new HttpRequest(httpStartLine, httpHeaders, httpCookies, httpBody);
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

    public Map<String, HttpFile> files() {
        return httpBody.files();
    }
}
