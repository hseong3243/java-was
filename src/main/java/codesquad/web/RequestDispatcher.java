package codesquad.web;

import codesquad.ContentTypes;
import codesquad.ServerHandler;
import codesquad.message.HttpRequest;
import codesquad.message.HttpResponse;
import codesquad.message.HttpStatusCode;
import codesquad.message.RuntimeIOException;
import codesquad.view.TemplateEngine;
import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestDispatcher implements ServerHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestDispatcher.class);
    public static final String HTTP_1_1 = "HTTP/1.1";

    private final AnnotationHandlerMapping handlerMapping;

    public RequestDispatcher(AnnotationHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public HttpResponse handle(HttpRequest httpRequest) {
        try {
            return dispatchInternal(httpRequest);
        } catch (Exception e) {
            return errorHandle(e);
        }
    }

    private HttpResponse dispatchInternal(HttpRequest httpRequest)
            throws InvocationTargetException, IllegalAccessException {
        log.debug("요청 경로에 따른 핸들러 메서드 조회");
        HandlerMethod handlerMethod = handlerMapping.getHandler(httpRequest);

        log.debug("핸들러 메서드 실행={}", handlerMethod);
        ModelAndView modelAndView = handlerMethod.invoke(httpRequest);

        log.debug("뷰 렌더링");
        String renderedView = TemplateEngine.render(new String(modelAndView.getView()), modelAndView);
        modelAndView.addHeader("Content-Length", String.valueOf(renderedView.getBytes().length));

        log.debug("HTTP 응답 메시지 생성");
        HttpResponse httpResponse = new HttpResponse(
                HTTP_1_1,
                modelAndView.getStatusCode(),
                renderedView);
        httpResponse.addHeaders(modelAndView.getHeaders());
        httpResponse.addHeader("Content-Type", getContentType(httpRequest.requestUrl()));
        return httpResponse;
    }

    private String getContentType(String requestUrl) {
        if (!requestUrl.contains(".")) {
            requestUrl = requestUrl + "/index.html";
        }
        int extensionStartIndex = requestUrl.indexOf(".");
        String fileNameExtension = requestUrl.substring(extensionStartIndex + 1);
        return ContentTypes.getMimeType(fileNameExtension);
    }

    private HttpResponse errorHandle(Exception e) {
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
        return httpResponse;
    }
}
