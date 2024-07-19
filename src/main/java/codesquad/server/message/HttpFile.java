package codesquad.server.message;

public class HttpFile {
    private final String fileName;
    private final String contentType;
    private final byte[] content;

    public HttpFile(String fileName, String contentType, byte[] content) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }
}
