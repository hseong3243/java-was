package codesquad.application.handler;

import codesquad.application.database.Database;
import codesquad.application.database.SessionStorage;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpStatusCode;
import codesquad.application.model.User;
import codesquad.application.web.ModelAndView;
import codesquad.application.web.RequestMapping;
import java.util.Map;
import java.util.NoSuchElementException;

public class LoginHandler {

    private final Database database;
    private final SessionStorage sessionStorage;

    public LoginHandler(Database database, SessionStorage sessionStorage) {
        this.database = database;
        this.sessionStorage = sessionStorage;
    }

    @RequestMapping(method = HttpMethod.POST, path = "/login")
    public ModelAndView login(HttpRequest httpRequest) {
        Data data = Data.from(httpRequest);
        User user;
        try {
            user = database.findUserByUserId(data.userId)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));
            user.validatePassword(data.password);
        } catch (NoSuchElementException e) {
            ModelAndView mav = new ModelAndView(HttpStatusCode.FOUND);
            mav.addHeader("Location", "/login/failed.html");
            return mav;
        }

        ModelAndView mav = new ModelAndView(HttpStatusCode.FOUND);
        mav.addHeader("Location", "/");
        mav.setCookie(sessionStorage.store(user), "/", true);
        return mav;
    }

    private record Data(String userId, String password) {
        private static Data from(HttpRequest httpRequest) {
            Map<String, String> data = httpRequest.bodyData();
            validate(data);
            return new Data(data.get("userId"), data.get("password"));
        }

        private static void validate(Map<String, String> data) {
            if(data.get("userId") != null && data.get("password") != null) {
                return;
            }
            throw new IllegalArgumentException("요청을 위한 데이터가 포함되어 있지 않습니다.");
        }
    }
}
