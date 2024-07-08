package codesquad.message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public record HttpHeaders(Map<String, String> headers) {

    private static final String LINE_SEPARATOR = "\n";

    public static HttpHeaders parse(String rawHeaders) {
        Map<String, String> headers = new HashMap<>();
        String[] splitHeaders = rawHeaders.split(LINE_SEPARATOR);
        for (String header : splitHeaders) {
            String[] keyValue = header.split(": ");
            validateHeader(keyValue);
            headers.put(keyValue[0], keyValue[1]);
        }
        return new HttpHeaders(headers);
    }

    private static void validateHeader(String[] keyValue) {
        if(checkKeyValue(keyValue)) {
            return;
        }
        throw new IllegalArgumentException("헤더 형식이 올바르지 않습니다. header=" + Arrays.toString(keyValue));
    }

    private static boolean checkKeyValue(String[] keyValue) {
        if(keyValue.length != 2) {
            return false;
        }
        if(keyValue[0].isBlank() || keyValue[1].isBlank()) {
            return false;
        }
        return true;
    }

    public String get(String key) {
        return headers.get(key);
    }
}
