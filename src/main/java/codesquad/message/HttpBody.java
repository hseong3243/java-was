package codesquad.message;

import java.util.HashMap;
import java.util.Map;

public record HttpBody(Map<String, String> data) {

    public static HttpBody parse(String rawMessageBody) {
        Map<String, String> bodies = new HashMap<>();
        String[] splitBody = rawMessageBody.split("&");
        for (String rawKeyValue : splitBody) {
            String[] keyValue = rawKeyValue.split("=");
            bodies.put(keyValue[0], keyValue[1]);
        }
        return new HttpBody(bodies);
    }
}
