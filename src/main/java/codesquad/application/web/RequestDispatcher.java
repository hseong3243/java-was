package codesquad.application.web;

import codesquad.application.database.SessionStorage;
import codesquad.application.view.TemplateEngine;
import codesquad.server.ServerHandler;
import codesquad.server.message.ContentTypes;
import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpResponse;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestDispatcher implements ServerHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestDispatcher.class);
    public static final String HTTP_1_1 = "HTTP/1.1";

    private final AnnotationHandlerMapping handlerMapping;
    private final SessionStorage sessionStorage;

    public RequestDispatcher(AnnotationHandlerMapping handlerMapping, SessionStorage sessionStorage) {
        this.handlerMapping = handlerMapping;
        this.sessionStorage = sessionStorage;
    }

    public HttpResponse handle(HttpRequest httpRequest) {
        try {
            return dispatchInternal(httpRequest);
        } catch (Throwable e) {
            return errorHandle(e);
        }
    }

    private HttpResponse dispatchInternal(HttpRequest httpRequest) throws Throwable {
        log.debug("사용자 세션 정보 체크");
        Optional<String> optionalSessionId = Optional.ofNullable(httpRequest.cookies().get("SID"));
        if (optionalSessionId.isPresent()) {
            String sessionId = optionalSessionId.get();
            if (sessionStorage.findLoginUser(sessionId).isEmpty()) {
                return HttpResponseFactory.invalidateSession();
            }
        }

        log.debug("요청 경로에 따른 핸들러 메서드 조회");
        HandlerMethod handlerMethod = handlerMapping.getHandler(httpRequest);

        log.debug("핸들러 메서드 실행={}", handlerMethod);
        ModelAndView modelAndView = handlerMethod.invoke(httpRequest);

        log.debug("뷰 렌더링");
        String contentType = getContentType(httpRequest.requestUrl());
        if(!contentType.contains("text/html")) {
            HttpResponse httpResponse = new HttpResponse(
                    HTTP_1_1,
                    modelAndView.getStatusCode(),
                    modelAndView.getView());
            httpResponse.addHeaders(modelAndView.getHeaders());
            httpResponse.addHeader("Content-Type", contentType);
            return httpResponse;
        }
        String renderedView = TemplateEngine.render(new String(modelAndView.getView()), modelAndView);
        modelAndView.addHeader("Content-Length", String.valueOf(renderedView.getBytes().length));

        log.debug("HTTP 응답 메시지 생성");
        HttpResponse httpResponse = new HttpResponse(
                HTTP_1_1,
                modelAndView.getStatusCode(),
                renderedView);
        httpResponse.addHeaders(modelAndView.getHeaders());
        httpResponse.addHeader("Content-Type", contentType);
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
            httpResponse = HttpResponseFactory.badRequest();
        } else if (e instanceof NoSuchElementException) {
            httpResponse = HttpResponseFactory.notFound();
        } else if (e instanceof MethodNotAllowedException methodNotAllowedException) {
            httpResponse = HttpResponseFactory.methodNotAllowed(methodNotAllowedException.getAllowedMethods());
        } else {
            httpResponse = HttpResponseFactory.internalServerError();
        }
        return httpResponse;
    }
}
