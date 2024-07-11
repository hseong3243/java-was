package codesquad;

import codesquad.handler.AnnotationHandlerMapping;
import codesquad.handler.HandlerMethod;
import codesquad.handler.ModelAndView;
import codesquad.message.HttpRequest;
import codesquad.message.HttpResponse;
import codesquad.message.HttpStatusCode;
import codesquad.message.RuntimeIOException;
import codesquad.view.TemplateEngine;
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
    private final AnnotationHandlerMapping annotationHandlerMapping;

    public ClientRequest(Socket clientSocket, AnnotationHandlerMapping annotationHandlerMapping) {
        this.clientSocket = clientSocket;
        this.annotationHandlerMapping = annotationHandlerMapping;
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream clientOutput = clientSocket.getOutputStream()
        ) {
            process(inputStream, clientOutput);
        } catch (IOException e) {
            log.error("입출력 예외가 발생했습니다.", e);
        }
    }

    private void process(InputStream clientInput, OutputStream clientOutput) throws IOException {
        try {
            log.debug("Client connected");

            // HTTP 요청을 파싱합니다.
            BufferedReader br = new BufferedReader(new InputStreamReader(clientInput));
            HttpRequest httpRequest = HttpRequest.parse(br);
            log.debug("Http request message={}", httpRequest);

            // 요청을 처리할 핸들러를 조회합니다.
            HandlerMethod handlerMethod = annotationHandlerMapping.getHandler(httpRequest);
            ModelAndView modelAndView = handlerMethod.invoke(httpRequest);

            // 뷰를 렌더링합니다.
            String renderedView = TemplateEngine.render(new String(modelAndView.getView()), modelAndView);
            modelAndView.addHeader("Content-Length", String.valueOf(renderedView.getBytes().length));

            // HTTP 응답을 생성합니다.
            HttpResponse httpResponse = new HttpResponse(
                    HTTP_1_1,
                    modelAndView.getStatusCode(),
                    renderedView);
            httpResponse.addHeaders(modelAndView.getHeaders());
            httpResponse.addHeader("Content-Type", getContentType(httpRequest.requestUrl()));
            httpResponse.write(clientOutput);

            clientOutput.flush();

            clientSocket.close();
        } catch (Exception e) {
            errorHandle(clientOutput, e);
        }
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
        log.warn("예외가 발생했습니다.", e);
        HttpResponse httpResponse;
        if (e instanceof IllegalArgumentException) {
            httpResponse = new HttpResponse(HTTP_1_1, HttpStatusCode.BAD_REQUEST, "올바르지 않은 요청입니다.");
        } else if (e instanceof NoSuchElementException) {
            httpResponse = new HttpResponse(HTTP_1_1, HttpStatusCode.NOT_FOUND, "존재하지 않는 리소스입니다.");
        } else if (e instanceof RuntimeIOException) {
            httpResponse = new HttpResponse(HTTP_1_1, HttpStatusCode.INTERNAL_SERVER_ERROR, "입출력 예외가 발생했습니다.");
        } else {
            httpResponse = new HttpResponse(HTTP_1_1, HttpStatusCode.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다.");
        }
        httpResponse.addHeader("Content-Type", "text/plain; charset=UTF-8");
        httpResponse.write(clientOutput);
    }
}
