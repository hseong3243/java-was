package codesquad.server.utils;

import codesquad.server.message.RuntimeIOException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class ByteUtils {

    public static byte[] readLine(InputStream inputStream) {
        List<Byte> result = new ArrayList<>();
        int count = 0;
        try {
            int b = 0;
            while ((b = inputStream.read()) != -1) {
                byte bytes = (byte) b;
                result.add(bytes);
                if (bytes == '\r') {
                    count = 1;
                }
                if (count == 1 && bytes == '\n') {
                    return toByteArray(result.subList(0, result.size() - 2));
                }
            }
            return toByteArray(result);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private static byte[] toByteArray(List<Byte> bytes) {
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }
}
