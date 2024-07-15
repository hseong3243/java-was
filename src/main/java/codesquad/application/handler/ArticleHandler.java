package codesquad.application.handler;

import codesquad.application.database.ArticleDatabase;
import codesquad.application.database.UserDatabase;
import codesquad.application.database.SessionStorage;
import codesquad.application.model.Article;
import codesquad.application.model.User;
import codesquad.application.util.ResourceUtils;
import codesquad.application.web.ModelAndView;
import codesquad.application.web.RequestMapping;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpStatusCode;
import java.util.NoSuchElementException;

public class ArticleHandler {

    private final ArticleDatabase articleDatabase;
    private final SessionStorage sessionStorage;
    private final UserDatabase userDatabase;

    public ArticleHandler(ArticleDatabase articleDatabase, SessionStorage sessionStorage, UserDatabase userDatabase) {
        this.articleDatabase = articleDatabase;
        this.sessionStorage = sessionStorage;
        this.userDatabase = userDatabase;
    }

    @RequestMapping(path = "/article", method = HttpMethod.GET)
    public ModelAndView getArticle(HttpRequest request) {
        String sessionId = request.cookies().get("SID");
        if(isInvalidSession(sessionId)) {
            return redirectToLogin();
        }
        return new ModelAndView(ResourceUtils.getStaticFile("/article/index.html"), HttpStatusCode.OK);
    }

    private record PostArticleData(String title, String content) {
        private static PostArticleData from(HttpRequest httpRequest) {
            String title = httpRequest.bodyData().get("title");
            String content = httpRequest.bodyData().get("content");
            if(title == null || title.isBlank()) {
                throw new IllegalArgumentException("게시글 제목은 공백일 수 없습니다.");
            }
            if(content == null || content.isBlank()) {
                throw new IllegalArgumentException("게시글 본문은 공백일 수 없습니다.");
            }
            return new PostArticleData(title, content);
        }
    }

    @RequestMapping(path = "/article", method = HttpMethod.POST)
    public ModelAndView postArticle(HttpRequest request) {
        String sessionId = request.cookies().get("SID");
        if(isInvalidSession(sessionId)) {
            return redirectToLogin();
        }
        PostArticleData data = PostArticleData.from(request);
        String userId = sessionStorage.findLoginUser(sessionId)
                .orElseThrow(() -> new NoSuchElementException("세션 정보가 유효하지 않습니다."));
        User user = userDatabase.findUserByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        Article article = Article.create(articleDatabase.getNextId(), data.title, data.content, user);
        articleDatabase.save(article);

        ModelAndView modelAndView = new ModelAndView(HttpStatusCode.MOVED_PERMANENTLY);
        modelAndView.addHeader("Location", "/");
        modelAndView.add("articleId", String.valueOf(article.getArticleId()));
        return modelAndView;
    }

    private boolean isInvalidSession(String sessionId) {
        return sessionId == null || sessionStorage.findLoginUser(sessionId).isEmpty();
    }

    private ModelAndView redirectToLogin() {
        ModelAndView modelAndView = new ModelAndView(HttpStatusCode.MOVED_PERMANENTLY);
        modelAndView.addHeader("Location", "/login");
        return modelAndView;
    }
}
