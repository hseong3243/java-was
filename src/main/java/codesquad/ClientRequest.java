package codesquad;

import codesquad.handler.Handler;
import codesquad.handler.HandlerMapper;
import codesquad.handler.ModelAndView;
import codesquad.message.HttpRequest;
import codesquad.message.HttpResponse;
import codesquad.message.HttpStatusCode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRequest implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ClientRequest.class);
    public static final String HTTP_1_1 = "HTTP/1.1";

    private final Socket clientSocket;

    public ClientRequest(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (OutputStream clientOutput = clientSocket.getOutputStream()) {
            process(clientOutput);
        } catch (IOException e) {
            log.error("입출력 예외가 발생했습니다.", e);
        }
    }

    private void process(OutputStream clientOutput) throws IOException {
        try {
            log.debug("Client connected");

            // HTTP 요청을 파싱합니다.
            HttpRequest requestMessage = getHttpRequest();
            log.debug("Http request message={}", requestMessage);

            // 요청을 처리할 핸들러를 조회합니다.
            Handler handler = HandlerMapper.mapping(requestMessage.requestUrl());
            ModelAndView modelAndView = handler.handle(requestMessage);

            // HTTP 응답을 생성합니다.
            HttpResponse httpResponse = new HttpResponse(
                    HTTP_1_1,
                    modelAndView.getStatusCode(),
                    modelAndView.getView());
            httpResponse.addHeader("Content-Type", getContentType(requestMessage.requestUrl()));
            clientOutput.write(httpResponse.toHttpMessage().getBytes());
            clientOutput.flush();

            clientSocket.close();
        } catch (Exception e) {
            errorHandle(clientOutput, e);
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

    private void errorHandle(OutputStream clientOutput, Exception e) throws IOException {
        HttpResponse httpResponse;
        if (e instanceof IllegalArgumentException) {
            httpResponse = new HttpResponse(HTTP_1_1, HttpStatusCode.BAD_REQUEST, "올바르지 않은 요청입니다.");
        } else if (e instanceof NoSuchElementException) {
            httpResponse = new HttpResponse(HTTP_1_1, HttpStatusCode.NOT_FOUND, "존재하지 않는 리소스입니다.");
        } else {
            httpResponse = new HttpResponse(HTTP_1_1, HttpStatusCode.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다.");
        }
        clientOutput.write(httpResponse.toHttpMessage().getBytes());
    }
}
