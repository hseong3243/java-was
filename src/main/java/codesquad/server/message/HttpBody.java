package codesquad.server.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public record HttpBody(Map<String, String> data) {

    public static HttpBody parse(BufferedReader br, HttpHeaders httpHeaders) throws IOException {
        Map<String, String> data = new HashMap<>();
        if (!httpHeaders.isFormData()) {
            return new HttpBody(data);
        }

        String body = getRawBodyUsing(br, httpHeaders);
        String[] splitBody = body.split("&");
        for (String rawKeyValue : splitBody) {
            String[] keyValue = rawKeyValue.split("=");
            data.put(decode(keyValue[0]), decode(keyValue[1]));
        }
        return new HttpBody(data);
    }

    private static String getRawBodyUsing(BufferedReader br, HttpHeaders httpHeaders) throws IOException {
        int contentLength = httpHeaders.get("Content-Length")
                .map(Integer::parseInt)
                .orElse(0);
        char[] buffer = new char[contentLength];
        br.read(buffer);
        String body = new String(buffer);
        return body;
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("문자 인코딩 타입이 잘못되었습니다.");
        }
    }
}
