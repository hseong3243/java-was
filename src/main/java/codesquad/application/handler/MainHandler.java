package codesquad.application.handler;

import codesquad.application.database.UserDatabase;
import codesquad.application.database.SessionStorage;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpStatusCode;
import codesquad.application.model.User;
import codesquad.application.util.ResourceUtils;
import codesquad.application.web.ModelAndView;
import codesquad.application.web.RequestMapping;
import java.util.NoSuchElementException;

public class MainHandler {

    private final UserDatabase userDatabase;
    private final SessionStorage sessionStorage;

    public MainHandler(UserDatabase userDatabase, SessionStorage sessionStorage) {
        this.userDatabase = userDatabase;
        this.sessionStorage = sessionStorage;
    }

    @RequestMapping(method = HttpMethod.GET, path = "/")
    public ModelAndView mainPage(HttpRequest httpRequest) {
        String sessionId = httpRequest.cookies().get("SID");
        ModelAndView modelAndView = new ModelAndView(ResourceUtils.getStaticFile("/index.html"), HttpStatusCode.OK);
        if(sessionId != null) {
            String userId = sessionStorage.findLoginUser(sessionId)
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
