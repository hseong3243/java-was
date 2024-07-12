package codesquad.server.message;

import java.util.HashMap;
import java.util.Map;

public record HttpCookies(Map<String, String> cookies) {

    public static HttpCookies parse(HttpHeaders httpHeaders) {
        String rawCookie = httpHeaders.get("Cookie").orElse("");
        Map<String, String> cookies = new HashMap<>();
        if(rawCookie.isBlank()) {
            return new HttpCookies(cookies);
        }

        String[] splitCookies = rawCookie.split(";");
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
