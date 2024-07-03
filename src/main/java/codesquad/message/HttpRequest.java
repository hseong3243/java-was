package codesquad.message;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public record HttpRequest(String method, String requestUrl,
                          Map<String, String> queries,
                          String httpVersion,
                          Map<String, String> header,
                          String body) {

    public static HttpRequest parse(String rawHttpMessage) {
        StringTokenizer st = new StringTokenizer(rawHttpMessage);
        String method = st.nextToken();
        String requestUrl = st.nextToken();
        String httpVersion = st.nextToken();

        // 쿼리를 분리한다.
        Map<String, String> queries = getQueries(requestUrl);

        Map<String, String> header = new HashMap<>();
        String key;
        String value;
        while (st.hasMoreTokens()) {
            key = st.nextToken();
            if (key.isBlank()) {
                break;
            }
            key = key.replace(":", "");
            value = st.nextToken();
            header.put(key, value);
        }
        return new HttpRequest(method, requestUrl, queries, httpVersion, header, "");
    }

    private static Map<String, String> getQueries(String requestUrl) {
        Map<String, String> queries = new HashMap<>();
        int substringPoint = requestUrl.indexOf("?") + 1;
        if(substringPoint == 0) {
            return queries;
        }
        String[] keyValues = requestUrl.substring(substringPoint).split("&");
        for (String keyValue : keyValues) {
            String[] keyAndValue = keyValue.split("=");
            queries.put(
                    URLDecoder.decode(keyAndValue[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(keyAndValue[1], StandardCharsets.UTF_8));
        }
        return queries;
    }
}
