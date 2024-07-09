package codesquad.handler;

import codesquad.message.HttpRequest;
import codesquad.message.HttpStatusCode;
import codesquad.model.User;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(CreateUserHandler.class);

    private record Data(String userId, String password, String name, String email) {
    }

    @Override
    public ModelAndView handle(HttpRequest httpRequest) {
        Data data = queriesToData(httpRequest);
        User user = User.create(data.userId, data.password, data.name, data.password);
        log.debug("새로운 유저가 생성되었습니다. userId={}", user.getUserId());

        ModelAndView modelAndView = new ModelAndView(HttpStatusCode.FOUND);
        modelAndView.addHeader("Location", "/");
        modelAndView.add("userId", user.getUserId());
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
}
