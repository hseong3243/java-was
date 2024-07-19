package codesquad.server.message;

import codesquad.server.utils.ByteUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record HttpHeaders(Map<String, String> headers) {

    private static final String LINE_SEPARATOR = "\n";

    public static HttpHeaders parse(InputStream clientInput) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String header;
        while (!(header = new String(ByteUtils.readLine(clientInput))).isEmpty()) {
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

    public Optional<String> get(String key) {
        return Optional.ofNullable(headers.get(key));
    }

    public boolean isFormData() {
        String contentType = headers.get("Content-Type");
        return contentType != null && contentType.equals("application/x-www-form-urlencoded");
    }

    public boolean isMultiPart() {
        String contentType = headers.get("Content-Type");
        return contentType != null && contentType.contains("multipart/form-data");
    }
}
