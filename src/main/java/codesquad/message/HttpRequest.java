package codesquad.message;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public record HttpRequest(String method, String requestUrl, String httpVersion, Map<String, String> header,
                          String body) {

    public static HttpRequest parse(String rawHttpMessage) {
        StringTokenizer st = new StringTokenizer(rawHttpMessage);
        String method = st.nextToken();
        String requestUrl = st.nextToken();
        String httpVersion = st.nextToken();

        Map<String, String> header = new HashMap<>();
        String key;
        String value;
        while (st.hasMoreTokens()) {
            key = st.nextToken();
            if(key.isBlank()) {
                break;
            }
            key = key.replace(":", "");
            value = st.nextToken();
            header.put(key, value);
        }
        return new HttpRequest(method, requestUrl, httpVersion, header, "");
    }
}
