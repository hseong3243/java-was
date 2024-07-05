package codesquad;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ContentTypes {

    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("js", "text/javascript");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("ico", "image/x-icon");
        MIME_TYPES.put("svg", "image/svg+xml");
    }

    private ContentTypes() {
    }

    public static String getMimeType(String fileNameExtension) {
        return Optional.ofNullable(MIME_TYPES.get(fileNameExtension))
                .orElseThrow(() -> new IllegalArgumentException(fileNameExtension + "는 지원하지 않는 확장자입니다."));
    }
}
