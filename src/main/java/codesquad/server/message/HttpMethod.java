package codesquad.server.message;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpMethod {
    GET, POST;

    public static HttpMethod find(String method) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("허용되지 않는 메서드입니다."));
    }
}
