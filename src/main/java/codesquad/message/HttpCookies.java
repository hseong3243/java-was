package codesquad.message;

import java.util.HashMap;
import java.util.Map;

public record HttpCookies(Map<String, String> cookies) {

    public static HttpCookies parse(String rawCookies) {
        Map<String, String> cookies = new HashMap<>();
        if(rawCookies == null || rawCookies.isBlank()) {
            return new HttpCookies(cookies);
        }

        String[] splitCookies = rawCookies.split(";");
        for (String cookie : splitCookies) {
            String[] keyValue = cookie.trim().split("=");
            if(keyValue.length != 2) {
                break;
            }
            cookies.put(keyValue[0], keyValue[1]);
        }
        return new HttpCookies(cookies);
    }
}
