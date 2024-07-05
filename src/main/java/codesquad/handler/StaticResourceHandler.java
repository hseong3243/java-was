package codesquad.handler;

import codesquad.message.HttpRequest;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(StaticResourceHandler.class);

    @Override
    public ModelAndView handle(HttpRequest httpRequest) {
        String viewPath = addIndexPath(httpRequest.requestUrl());
        String view = getStaticFile(viewPath);
        return new ModelAndView(view);
    }

    private String addIndexPath(String requestUrl) {
        if (requestUrl.contains(".")) {
            return requestUrl;
        }
        if (requestUrl.endsWith("/")) {
            return requestUrl + "index.html";
        }
        return requestUrl + "/index.html";
    }

    private String getStaticFile(String resourcePath) {
        try {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("static" + resourcePath);
            return new String(resourceAsStream.readAllBytes());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("유효하지 않은 경로입니다. path=" + resourcePath);
        } catch (IOException e) {
            throw new RuntimeException("입출력 예외가 발생했습니다.", e);
        }
    }
}
