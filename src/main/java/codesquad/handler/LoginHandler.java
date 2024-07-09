package codesquad.handler;

import codesquad.database.DataBase;
import codesquad.database.SessionStorage;
import codesquad.message.HttpRequest;
import codesquad.message.HttpStatusCode;
import codesquad.model.User;
import java.util.Map;
import java.util.NoSuchElementException;

public class LoginHandler implements Handler{

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

    @Override
    public ModelAndView handle(HttpRequest httpRequest) {
        Data data = Data.from(httpRequest);
        User user;
        try {
            user = DataBase.findUserByUserId(data.userId)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));
            user.validatePassword(data.password);
        } catch (NoSuchElementException e) {
            ModelAndView mav = new ModelAndView(HttpStatusCode.FOUND);
            mav.addHeader("Location", "/login/failed.html");
            return mav;
        }

        ModelAndView mav = new ModelAndView(HttpStatusCode.FOUND);
        mav.addHeader("Location", "/");
        mav.setCookie(SessionStorage.store(user), "/", true);
        return mav;
    }
}
