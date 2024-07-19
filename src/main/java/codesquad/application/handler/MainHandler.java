package codesquad.application.handler;

import codesquad.application.database.ArticleDatabase;
import codesquad.application.database.SessionStorage;
import codesquad.application.database.UserDatabase;
import codesquad.application.model.Article;
import codesquad.application.model.User;
import codesquad.application.util.ResourceUtils;
import codesquad.application.web.ModelAndView;
import codesquad.application.web.RequestMapping;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpRequest;
import java.util.List;
import java.util.NoSuchElementException;

public class MainHandler {

    private final UserDatabase userDatabase;
    private final SessionStorage sessionStorage;
    private final ArticleDatabase articleDatabase;

    public MainHandler(UserDatabase userDatabase, SessionStorage sessionStorage, ArticleDatabase articleDatabase) {
        this.userDatabase = userDatabase;
        this.sessionStorage = sessionStorage;
        this.articleDatabase = articleDatabase;
    }

    @RequestMapping(path = "/", method = HttpMethod.GET)
    public ModelAndView getArticles(HttpRequest request) {
        String sessionId = request.cookies().get("SID");
        ModelAndView modelAndView = new ModelAndView(ResourceUtils.getStaticFile("/article/articleList.html"));
        if(sessionId != null) {
            String userId = sessionStorage.findLoginUser(sessionId)
                    .orElseThrow(() -> new NoSuchElementException("세션이 유효하지 않습니다."));
            User user = userDatabase.findUserByUserId(userId)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
            modelAndView.add("userId", user.getUserId());
            modelAndView.add("name", user.getName());
            modelAndView.add("email", user.getEmail());
        }

        List<Article> articles = articleDatabase.findAll();
        modelAndView.add("articles", articles);
        return modelAndView;
    }
}
