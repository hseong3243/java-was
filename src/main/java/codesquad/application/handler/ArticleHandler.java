package codesquad.application.handler;

import codesquad.application.database.SessionStorage;
import codesquad.application.util.ResourceUtils;
import codesquad.application.web.ModelAndView;
import codesquad.application.web.RequestMapping;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpStatusCode;

public class ArticleHandler {

    private final SessionStorage sessionStorage;

    public ArticleHandler(SessionStorage sessionStorage) {
        this.sessionStorage = sessionStorage;
    }

    @RequestMapping(path = "/article", method = HttpMethod.GET)
    public ModelAndView getArticle(HttpRequest request) {
        String sessionId = request.cookies().get("SID");
        if(sessionId == null || sessionStorage.findLoginUser(sessionId).isEmpty()) {
            ModelAndView modelAndView = new ModelAndView(HttpStatusCode.MOVED_PERMANENTLY);
            modelAndView.addHeader("Location", "/login");
            return modelAndView;
        }
        return new ModelAndView(ResourceUtils.getStaticFile("/article/index.html"), HttpStatusCode.OK);
    }
}
