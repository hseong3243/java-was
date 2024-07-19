package codesquad.application.web;

import codesquad.application.file.ImageStore;
import codesquad.server.message.HttpRequest;
import codesquad.application.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(StaticResourceHandler.class);

    private final ImageStore imageStore;

    public StaticResourceHandler(ImageStore imageStore) {
        this.imageStore = imageStore;
    }

    @Override
    public ModelAndView handle(HttpRequest httpRequest) {
        String viewPath = addIndexPath(httpRequest.requestUrl());
        if(viewPath.contains("/images")) {
            String filename = viewPath.replace("/images/", "");
            byte[] image = imageStore.getImage(filename);
            ModelAndView modelAndView = new ModelAndView(image);
            modelAndView.addHeader("Content-Length", String.valueOf(image.length));
            modelAndView.addHeader("Content-Type", "image/png");
            return modelAndView;
        }
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
