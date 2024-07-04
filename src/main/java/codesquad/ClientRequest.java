package codesquad;

import codesquad.handler.Handler;
import codesquad.handler.HandlerMapper;
import codesquad.handler.ModelAndView;
import codesquad.message.HttpRequest;
import codesquad.message.HttpResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
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

            // 요청을 처리할 핸들러를 조회합니다.
            Handler handler = HandlerMapper.mapping(requestMessage.requestUrl());
            ModelAndView modelAndView = handler.handle(requestMessage);

            // HTTP 응답을 생성합니다.
            OutputStream clientOutput = clientSocket.getOutputStream();
            HttpResponse httpResponse = new HttpResponse(
                    "HTTP/1.1",
                    modelAndView.getStatusCode(),
                    modelAndView.getView());
            httpResponse.addHeader("Content-Type", getContentType(requestMessage.requestUrl()));
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

    private String readHttpRequestMessage(InputStream clientInput) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(clientInput));
        StringBuilder sb = new StringBuilder();
        String readLine;
        while (!(readLine = br.readLine()).isEmpty()) {
            sb.append(readLine).append("\n");
        }
        return sb.toString();
    }

    private String getContentType(String requestUrl) {
        if (!requestUrl.contains(".")) {
            requestUrl = requestUrl + "/index.html";
        }
        int extensionStartIndex = requestUrl.indexOf(".");
        String fileNameExtension = requestUrl.substring(extensionStartIndex + 1);
        return ContentTypes.getMimeType(fileNameExtension);
    }
}
