package codesquad.handler;

import codesquad.database.DataBase;
import codesquad.database.UserDatabase;
import codesquad.database.UserSessionStorage;
import codesquad.message.HttpRequest;
import codesquad.message.HttpStatusCode;
import codesquad.model.User;
import codesquad.util.ResourceUtils;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

    private final UserDatabase userDatabase;
    private final UserSessionStorage userSessionStorage;

    public UserHandler(UserDatabase userDatabase, UserSessionStorage userSessionStorage) {
        this.userDatabase = userDatabase;
        this.userSessionStorage = userSessionStorage;
    }

    private record Data(String userId, String password, String name, String email) {
    }

    @PostMapping("/user/create")
    public ModelAndView createUser(HttpRequest httpRequest) {
        Data data = queriesToData(httpRequest);
        User user = User.create(data.userId, data.password, data.name, data.password);
        log.debug("새로운 유저가 생성되었습니다. userId={}", user.getUserId());

        ModelAndView modelAndView = new ModelAndView(HttpStatusCode.FOUND);
        modelAndView.addHeader("Location", "/");
        modelAndView.add("userId", user.getUserId());
        DataBase.addUser(user);
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

    @GetMapping("/user/list")
    public ModelAndView listUser(HttpRequest httpRequest) {
        String sessionId = httpRequest.cookies().get("SID");
        if (sessionId == null) {
            ModelAndView mav = new ModelAndView(HttpStatusCode.FOUND);
            mav.addHeader("Location", "/login");
            return mav;
        }
        StringBuilder sb = new StringBuilder();
        for (User user : DataBase.findAll()) {
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
