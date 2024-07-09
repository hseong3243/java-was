package codesquad.message;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public record HttpBody(Map<String, String> data) {

    public static HttpBody parse(String rawMessageBody) {
        Map<String, String> bodies = new HashMap<>();
        String[] splitBody = rawMessageBody.split("&");
        for (String rawKeyValue : splitBody) {
            String[] keyValue = rawKeyValue.split("=");
            bodies.put(decode(keyValue[0]), decode(keyValue[1]));
        }
        return new HttpBody(bodies);
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("문자 인코딩 타입이 잘못되었습니다.");
        }
    }
}
