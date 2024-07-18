package codesquad.server.utils;

import codesquad.server.message.HttpBody;
import codesquad.server.message.HttpFile;
import codesquad.server.message.HttpHeaders;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiPartParser {

    private static final byte[] NEW_LINE = ("\r\n").getBytes();
    private static final Logger log = LoggerFactory.getLogger(MultiPartParser.class);

    public static HttpBody parse(HttpHeaders httpHeaders, InputStream clientInput)
            throws IOException {
        Map<String, HttpFile> files = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        log.debug("multipart 파싱 시작");

        byte[] bodyBytes = readBytes(httpHeaders, clientInput);
        String contentType = httpHeaders.get("Content-Type").get();

        String boundary = contentType.split("boundary=", 2)[1].trim();
        log.debug("bounday={}", boundary);
        byte[] delimiter = ("--" + boundary).getBytes();
        byte[] endDelimiter = ("--" + boundary + "--").getBytes();

        int start = 0;
        int delimiterPos = indexOf(bodyBytes, delimiter, start);
        int endDelimiterPos = indexOf(bodyBytes, endDelimiter, start);
        while (delimiterPos != endDelimiterPos) {
            start = delimiterPos + delimiter.length + NEW_LINE.length; // part의 시작점
            delimiterPos = indexOf(bodyBytes, delimiter, start); // part의 경계

            int partHeaderEnd = indexOf(bodyBytes, NEW_LINE, start); // part header의 CRLF 위치
            String contentDisposition = new String(Arrays.copyOfRange(bodyBytes, start, partHeaderEnd));
            log.debug("Content-Disposition={}", contentDisposition);

            if (contentDisposition.contains("filename=")) {
                processFilePart(contentDisposition, bodyBytes, partHeaderEnd, delimiterPos, files);
            } else {
                processParameterPart(contentDisposition, bodyBytes, partHeaderEnd, delimiterPos, params);
            }
        }

        return new HttpBody(params, files);
    }

    private static byte[] readBytes(HttpHeaders httpHeaders, InputStream clientInput) throws IOException {
        int contentLength = Integer.parseInt(httpHeaders.get("Content-Length").get());
        return clientInput.readNBytes(contentLength);
    }

    private static int indexOf(byte[] body, byte[] target, int start) {
        for (int i = start; i < body.length - target.length; i++) {
            boolean found = true;
            for (int j = 0; j < target.length; j++) {
                if (body[i + j] != target[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return i;
            }
        }
        return -1;
    }

    private static void processParameterPart(String contentDisposition, byte[] body, int partHeaderEnd,
                                             int delimiterPos, Map<String, String> params) {
        String name = getFieldName(contentDisposition.split(";")[1]);
        String data = new String(
                Arrays.copyOfRange(body, partHeaderEnd + NEW_LINE.length * 2, delimiterPos - NEW_LINE.length));
        log.debug("name={}, data={}", name, data);
        params.put(name, data);
    }

    private static void processFilePart(String contentDisposition, byte[] body, int partHeaderEnd,
                                        int delimiterPos, Map<String, HttpFile> files) {
        String name = getFieldName(contentDisposition.split(";")[1]);
        String filename = getFileName(contentDisposition.split(";")[2]);
        int contentTypeNewLinePos = indexOf(body, NEW_LINE,
                partHeaderEnd + NEW_LINE.length); // content-type \r\n 첫 위치
        String partContentType = new String(
                Arrays.copyOfRange(body, partHeaderEnd + NEW_LINE.length, contentTypeNewLinePos));
        partContentType = partContentType.split(":")[1].trim();
        byte[] fileBytes = Arrays.copyOfRange(
                body, contentTypeNewLinePos + NEW_LINE.length * 2, delimiterPos - NEW_LINE.length);
        log.debug("content-type={}, filename={}, length={}", partContentType, filename, fileBytes.length);
        files.put(name, new HttpFile(filename, partContentType, fileBytes));
    }

    private static String getFieldName(String contentDisposition) {
        return contentDisposition.substring(contentDisposition.indexOf("name=") + "name=".length())
                .replaceAll("\"", "")
                .trim();
    }

    private static String getFileName(String contentDisposition) {
        return contentDisposition.substring(contentDisposition.indexOf("filename=") + "filename=".length())
                .replaceAll("\"", "")
                .trim();
    }
}
