package codesquad.handler;

import codesquad.database.DataBase;
import codesquad.database.SessionStorage;
import codesquad.message.HttpRequest;
import codesquad.model.User;
import codesquad.util.ResourceUtils;
import java.util.Map;
import java.util.NoSuchElementException;
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
        addUserInfoToModel(httpRequest, viewPath, modelAndView);
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

    private void addUserInfoToModel(HttpRequest httpRequest, String viewPath, ModelAndView modelAndView) {
        Map<String, String> cookies = httpRequest.cookies();
        if (viewPath.contains("html") && cookies.containsKey("SID")) {
            String sessionId = cookies.get("SID");
            if (!SessionStorage.isValid(sessionId)) {
                modelAndView.invalidateCookie();
                return;
            }
            String userId = SessionStorage.findLoginUser(sessionId)
                    .orElseThrow(() -> new NoSuchElementException("세션이 유효하지 않습니다."));
            User user = DataBase.findUserByUserId(userId)
                    .orElseThrow(() -> new NoSuchElementException("유저 정보가 유효하지 않습니다."));
            modelAndView.add("userId", user.getUserId());
            modelAndView.add("name", user.getName());
            modelAndView.add("email", user.getEmail());
        }
    }
}
