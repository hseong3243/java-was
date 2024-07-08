package codesquad.message;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public record HttpStartLine(String method, String path, Map<String, String> queries, String version) {

    public static HttpStartLine parse(String rawStartLine) {
        String[] splitLine = rawStartLine.split(" ");
        checkFormat(splitLine);

        String method = splitLine[0];
        String pathWithQueries = splitLine[1];
        String version = splitLine[2];

        URI uri = URI.create(pathWithQueries);
        String path = uri.getPath();
        String queryString = uri.getQuery();
        Map<String, String> queries = parseQueries(queryString);
        return new HttpStartLine(method, path, queries, version);
    }

    private static void checkFormat(String[] splitLine) {
        if(splitLine.length == 3) {
            return;
        }
        throw new IllegalArgumentException("HTTP 요청 개행 형식에 맞지 않습니다. startLine=" + Arrays.toString(splitLine));
    }

    private static Map<String, String> parseQueries(String queryString) {
        Map<String, String> queries = new HashMap<>();
        if(queryString == null || queryString.isBlank()) {
            return queries;
        }
        String[] splitQueries = queryString.split("&");
        for (String query : splitQueries) {
            String[] keyValue = query.split("=");
            checkQuery(keyValue);
            queries.put(decode(keyValue[0]), decode(keyValue[1]));
        }
        return queries;
    }

    private static void checkQuery(String[] keyValue) {
        if(keyValue.length == 2) {
            return;
        }
        throw new IllegalArgumentException("쿼리 파라미터가 형식에 맞지 안습니다. keyValue=" + Arrays.toString(keyValue));
    }

    private static String decode(String rawValue) {
        try {
            return URLDecoder.decode(rawValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("문자 인코딩 타입이 잘못되었습니다.");
        }
    }
}
