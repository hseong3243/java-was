package codesquad.server.message;

import codesquad.server.utils.MultiPartParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record HttpBody(Map<String, String> data, Map<String, HttpFile> files) {

    private static final Logger log = LoggerFactory.getLogger(HttpBody.class);

    public static HttpBody parse(BufferedReader br, InputStream clientInput, HttpHeaders httpHeaders) throws IOException {
        Map<String, String> data = new HashMap<>();

        if (httpHeaders.isMultiPart()) {
            HttpBody parse = MultiPartParser.parse(httpHeaders, clientInput);
            log.debug("폼 데이터={}", parse);
            return parse;
        }

        if (!httpHeaders.isFormData()) {
            return new HttpBody(data, null);
        }

        String body = getRawBodyUsing(br, httpHeaders);
        String[] splitBody = body.split("&");
        for (String rawKeyValue : splitBody) {
            String[] keyValue = rawKeyValue.split("=");
            keyValue = changeValueToEmptyString(keyValue);
            data.put(decode(keyValue[0]), decode(keyValue[1]));
        }
        return new HttpBody(data, null);
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

    private static String[] changeValueToEmptyString(String[] keyValue) {
        if (keyValue.length == 2) {
            return keyValue;
        }
        return new String[]{keyValue[0], ""};
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("문자 인코딩 타입이 잘못되었습니다.");
        }
    }
}
