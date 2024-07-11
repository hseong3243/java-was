package codesquad.handler;

import codesquad.database.UserDatabase;
import codesquad.database.UserSessionStorage;
import codesquad.message.HttpMethod;
import codesquad.message.HttpRequest;
import codesquad.message.HttpStatusCode;
import codesquad.model.User;
import codesquad.util.ResourceUtils;
import java.util.NoSuchElementException;

public class MainHandler {

    private final UserDatabase userDatabase;
    private final UserSessionStorage userSessionStorage;

    public MainHandler(UserDatabase userDatabase, UserSessionStorage userSessionStorage) {
        this.userDatabase = userDatabase;
        this.userSessionStorage = userSessionStorage;
    }

    @RequestMapping(method = HttpMethod.GET, path = "/")
    public ModelAndView mainPage(HttpRequest httpRequest) {
        String sessionId = httpRequest.cookies().get("SID");
        ModelAndView modelAndView = new ModelAndView(ResourceUtils.getStaticFile("/index.html"), HttpStatusCode.OK);
        if(sessionId != null) {
            String userId = userSessionStorage.findLoginUser(sessionId)
                    .orElseThrow(() -> new NoSuchElementException("세션이 유효하지 않습니다."));
            User user = userDatabase.findUserByUserId(userId)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
            modelAndView.add("userId", user.getUserId());
            modelAndView.add("name", user.getName());
            modelAndView.add("email", user.getEmail());
        }
        return modelAndView;
    }
}
