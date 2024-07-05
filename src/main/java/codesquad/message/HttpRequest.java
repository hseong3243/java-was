package codesquad.message;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public record HttpRequest(String method, String requestUrl,
                          Map<String, String> queries,
                          String httpVersion,
                          Map<String, String> header,
                          String body) {

    private static final String CSRF = "\r\n";

    public static HttpRequest parse(String rawHttpMessage) {
        String[] splitMessage = rawHttpMessage.split(CSRF);

        String[] startLineArr = splitMessage[0].split(" ");
        String method = startLineArr[0];
        String requestUrl = startLineArr[1];
        String httpVersion = startLineArr[2];

        // 쿼리를 분리한다.
        URI uri = URI.create(requestUrl);
        Map<String, String> queries = getQueries(uri.getQuery());
        requestUrl = uri.getPath();

        Map<String, String> header = new HashMap<>();
        for(int i=1; i<splitMessage.length; i++) {
            String rawHeader = splitMessage[i];
            if(rawHeader.equals(CSRF)) {
                break;
            }
            String[] splitHeader = rawHeader.split(": ");
            header.put(splitHeader[0], splitHeader[1]);
        }

        return new HttpRequest(method, requestUrl, queries, httpVersion, header, "");
    }

    private static Map<String, String> getQueries(String queryString) {
        Map<String, String> queries = new HashMap<>();
        if(queryString == null || queryString.isBlank()) {
            return queries;
        }
        String[] keyValues = queryString.split("&");
        for (String keyValue : keyValues) {
            checkQuery(keyValue);
            String[] keyAndValue = keyValue.split("=");
            queries.put(
                    URLDecoder.decode(keyAndValue[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(keyAndValue[1], StandardCharsets.UTF_8));
        }
        return queries;
    }

    private static void checkQuery(String keyValue) {
        if (keyValue.contains("=")) {
            return;
        }
        throw new IllegalArgumentException("쿼리 마라미터가 올바르지 않습니다.");
    }
}
