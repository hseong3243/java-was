package codesquad.handler;

import codesquad.util.ResourceUtils;
import codesquad.message.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(StaticResourceHandler.class);

    @Override
    public ModelAndView handle(HttpRequest httpRequest) {
        String viewPath = addIndexPath(httpRequest.requestUrl());
        byte[] view = ResourceUtils.getStaticFile(viewPath);
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
}
