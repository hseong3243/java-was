package codesquad;

import codesquad.message.HttpRequest;
import codesquad.message.HttpResponse;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRequest implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ClientRequest.class);

    private final Socket clientSocket;

    public ClientRequest(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            log.debug("Client connected");

            // HTTP 요청을 파싱합니다.
            HttpRequest requestMessage = getHttpRequest();
            log.debug("Http request message={}", requestMessage);

            // 요청 경로에 따라 정적 html 파일 경로를 생성합니다.
            String requestUrl = requestMessage.requestUrl();
            if(!requestUrl.contains(".")) {
                requestUrl = requestUrl + "/index.html";
            }

            // HTTP 응답을 생성합니다.
            OutputStream clientOutput = clientSocket.getOutputStream();
            HttpResponse httpResponse = new HttpResponse(
                    "HTTP/1.1",
                    200,
                    "OK",
                    getStaticFile(requestUrl));
            httpResponse.addHeader("Content-Type", getContentType(requestUrl));
            clientOutput.write(httpResponse.toHttpMessage().getBytes());
            clientOutput.flush();

            clientSocket.close();
        } catch (IOException e) {
            log.warn("입출력 예외가 발생했습니다.", e);
        }
    }

    private HttpRequest getHttpRequest() throws IOException {
        InputStream clientInput = clientSocket.getInputStream();
        String rawHttpRequestMessage = readHttpRequestMessage(clientInput);
        return HttpRequest.parse(rawHttpRequestMessage);
    }

    private  String readHttpRequestMessage(InputStream clientInput) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(clientInput));
        StringBuilder sb = new StringBuilder();
        String readLine;
        while (!(readLine = br.readLine()).isEmpty()) {
            sb.append(readLine).append("\n");
        }
        return sb.toString();
    }

    private  String getContentType(String requestUrl) {
        int extensionStartIndex = requestUrl.indexOf(".");
        String fileNameExtension = requestUrl.substring(extensionStartIndex + 1);
        return ContentTypes.getMimeType(fileNameExtension);
    }

    private String getStaticFile(String resourcePath) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/" + resourcePath);
        try (FileInputStream fileInputStream = new FileInputStream(resource.getPath())) {
            return new String(fileInputStream.readAllBytes());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("유효하지 않은 경로입니다. path=" + resourcePath);
        }
    }
}
