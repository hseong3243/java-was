package codesquad.application.web;

import codesquad.application.util.ResourceUtils;
import codesquad.application.view.TemplateEngine;
import codesquad.server.ServerHandler;
import codesquad.server.message.ContentTypes;
import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpResponse;
import codesquad.server.message.HttpStatusCode;
import codesquad.server.message.RuntimeIOException;
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
        } catch (Throwable e) {
            return errorHandle(e);
        }
    }

    private HttpResponse dispatchInternal(HttpRequest httpRequest) throws Throwable {
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

    private HttpResponse errorHandle(Throwable e) {
        log.warn("예외가 발생했습니다.", e);
        HttpResponse httpResponse;
        if (e instanceof IllegalArgumentException) {
            httpResponse = new HttpResponse(HTTP_1_1, HttpStatusCode.BAD_REQUEST, "올바르지 않은 요청입니다.");
            httpResponse.addHeader("Content-Type", "text/plain; charset=UTF-8");
        } else if (e instanceof NoSuchElementException) {
            httpResponse = new HttpResponse(HTTP_1_1, HttpStatusCode.NOT_FOUND,
                    ResourceUtils.getStaticFile("/error/notfound.html"));
            httpResponse.addHeader("Content-Type", "text/html; charset=UTF-8");
        } else if (e instanceof RuntimeIOException) {
            httpResponse = new HttpResponse(HTTP_1_1, HttpStatusCode.INTERNAL_SERVER_ERROR, "입출력 예외가 발생했습니다.");
            httpResponse.addHeader("Content-Type", "text/plain; charset=UTF-8");
        } else if(e instanceof MethodNotAllowedException methodNotAllowedException) {
            httpResponse = new HttpResponse(HTTP_1_1, HttpStatusCode.METHOD_NOT_ALLOWED,
                    ResourceUtils.getStaticFile("/error/methodNotAllowed.html"));
            httpResponse.addHeader("Content-Type", "text/html; charset=UTF-8");
            StringBuilder sb = new StringBuilder();
            methodNotAllowedException.getAllowedMethods()
                            .forEach(method -> sb.append(method.name()).append("; "));
            httpResponse.addHeader("Allow", sb.toString());
        } else {
            httpResponse = new HttpResponse(HTTP_1_1, HttpStatusCode.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다.");
            httpResponse.addHeader("Content-Type", "text/plain; charset=UTF-8");
        }
        return httpResponse;
    }
}
