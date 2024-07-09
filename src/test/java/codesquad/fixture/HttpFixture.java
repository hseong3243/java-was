package codesquad.fixture;

import codesquad.message.HttpMethod;
import java.util.HashMap;
import java.util.Map;

public class HttpFixture {

    public static HttpBuilder builder() {
        return new HttpBuilder();
    }

    public static class HttpBuilder {

        private HttpMethod method = HttpMethod.GET;
        private String path = "/";
        private Map<String, String> headers = new HashMap<>();
        private Map<String, String> cookies = new HashMap<>();
        private String body = "";

        public HttpBuilder() {
        }

        public HttpBuilder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public HttpBuilder path(String path) {
            this.path = path;
            return this;
        }

        public HttpBuilder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public HttpBuilder cookie(String key, String value) {
            this.cookies.put(key, value);
            return this;
        }

        public HttpBuilder body(String body) {
            this.body = body;
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            headers.put("Content-Length", String.valueOf(body.getBytes().length));
            return this;
        }

        public String build() {
            return  """
                    {method} {path} HTTP/1.1
                    {header}
                    {body}"""
                    .replace("{method}", method.name())
                    .replace("{path}", path)
                    .replace("{header}", toRequestHeader())
                    .replace("{body}", body);
        }

        private String toRequestHeader() {
            StringBuilder sb = new StringBuilder();
            headers.forEach((key, value) -> {
                sb.append(key).append(": ").append(value).append("\n");
            });
            return sb.toString();
        }

    }
}
