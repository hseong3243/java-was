package codesquad.application.handler;

import codesquad.application.database.SessionStorage;
import codesquad.application.database.UserDatabase;
import codesquad.application.model.User;
import codesquad.application.util.ResourceUtils;
import codesquad.application.web.ModelAndView;
import codesquad.application.web.RequestMapping;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpStatusCode;
import java.util.Map;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

    private final UserDatabase userDatabase;
    private final SessionStorage sessionStorage;

    public UserHandler(UserDatabase userDatabase, SessionStorage sessionStorage) {
        this.userDatabase = userDatabase;
        this.sessionStorage = sessionStorage;
    }

    private record Data(String userId, String password, String name, String email) {
    }

    @RequestMapping(method = HttpMethod.POST, path = "/user/create")
    public ModelAndView createUser(HttpRequest httpRequest) {
        Data data = queriesToData(httpRequest);
        User user = User.create(data.userId, data.password, data.name, data.password);
        log.debug("새로운 유저가 생성되었습니다. userId={}", user.getUserId());

        ModelAndView modelAndView = new ModelAndView(HttpStatusCode.FOUND);
        modelAndView.addHeader("Location", "/");
        modelAndView.add("userId", user.getUserId());
        userDatabase.addUser(user);
        return modelAndView;
    }

    private Data queriesToData(HttpRequest httpRequest) {
        Map<String, String> queries = httpRequest.bodyData();
        String userId = queries.get("userId");
        String password = queries.get("password");
        String name = queries.get("name");
        String email = queries.get("email");
        return new Data(userId, password, name, email);
    }

    @RequestMapping(method = HttpMethod.GET, path = "/user/list")
    public ModelAndView listUser(HttpRequest httpRequest) {
        String sessionId = httpRequest.cookies().get("SID");
        if (sessionId == null) {
            ModelAndView mav = new ModelAndView(HttpStatusCode.FOUND);
            mav.addHeader("Location", "/login");
            return mav;
        }

        sessionStorage.findLoginUser(sessionId)
                .orElseThrow(() -> new NoSuchElementException("세션 정보가 유효하지 않습니다."));
        StringBuilder sb = new StringBuilder();
        for (User user : userDatabase.findAll()) {
            sb.append("[UserId=").append(user.getUserId()).append(", ")
                    .append("Name=").append(user.getName()).append(", ")
                    .append("Email=").append(user.getEmail()).append("]")
                    .append("\n");
        }

        ModelAndView mav = new ModelAndView(ResourceUtils.getStaticFile("/user/user-list.html"), HttpStatusCode.OK);
        mav.add("userList", sb.toString());
        return mav;
    }
}
