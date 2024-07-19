package codesquad.application.util;

import codesquad.server.message.RuntimeIOException;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

public final class ResourceUtils {

    public static byte[] getStaticFile(String resourcePath) {
        try {
            InputStream resourceAsStream = ResourceUtils.class.getClassLoader().getResourceAsStream("static" + resourcePath);
            return resourceAsStream.readAllBytes();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("유효하지 않은 경로입니다. path=" + resourcePath);
        } catch (IOException e) {
            throw new RuntimeIOException("입출력 예외가 발생했습니다.", e);
        }
    }

    private ResourceUtils() {
    }
}
