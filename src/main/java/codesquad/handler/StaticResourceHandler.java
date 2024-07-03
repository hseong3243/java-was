package codesquad.handler;

import codesquad.message.HttpRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class StaticResourceHandler implements Handler {
    @Override
    public ModelAndView handle(HttpRequest httpRequest) {
        String viewPath = addIndexPath(httpRequest.requestUrl());
        String view = getStaticFile(viewPath);
        return new ModelAndView(view);
    }

    private String addIndexPath(String requestUrl) {
        if (!requestUrl.contains(".")) {
            return requestUrl + "/index.html";
        }
        return requestUrl;
    }

    private String getStaticFile(String resourcePath) {
        URL resource = getClass().getClassLoader().getResource("static/" + resourcePath);
        try (FileInputStream fileInputStream = new FileInputStream(resource.getPath())) {
            return new String(fileInputStream.readAllBytes());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("유효하지 않은 경로입니다. path=" + resourcePath);
        } catch (IOException e) {
            throw new RuntimeException("입출력 예외가 발생했습니다.", e);
        }
    }
}
