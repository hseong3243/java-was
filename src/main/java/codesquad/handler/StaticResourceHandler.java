package codesquad.handler;

import codesquad.message.HttpRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(StaticResourceHandler.class);

    @Override
    public ModelAndView handle(HttpRequest httpRequest) {
        String viewPath = addIndexPath(httpRequest.requestUrl());
        byte[] view = getStaticFile(viewPath);
        ModelAndView modelAndView = new ModelAndView(view);
        modelAndView.addHeader("Content-Length", String.valueOf(view.length));
        return modelAndView;
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

    private byte[] getStaticFile(String resourcePath) {
        try {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("static" + resourcePath);
            return resourceAsStream.readAllBytes();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("유효하지 않은 경로입니다. path=" + resourcePath);
        } catch (IOException e) {
            throw new RuntimeException("입출력 예외가 발생했습니다.", e);
        }
    }
}
