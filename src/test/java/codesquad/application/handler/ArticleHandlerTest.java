package codesquad.application.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.application.database.SessionStorage;
import codesquad.application.model.User;
import codesquad.application.util.ResourceUtils;
import codesquad.application.web.ModelAndView;
import codesquad.fixture.HttpFixture;
import codesquad.fixture.UserFixture;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpStatusCode;
import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ArticleHandlerTest {

    private ArticleHandler articleHandler;
    private SessionStorage sessionStorage;

    @BeforeEach
    void setUp() {
        sessionStorage = new SessionStorage();
        articleHandler = new ArticleHandler(sessionStorage);
    }

    @Nested
    @DisplayName("getArticle 호출 시")
    class GetArticleTest {

        @Test
        @DisplayName("로그인한 경우 글쓰기 폼 html을 반환한다.")
        void returnArticleFormWhenUserLogin() {
            //given
            User user = UserFixture.user();
            String sessionId = sessionStorage.store(user);
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/article")
                    .cookie("SID", sessionId)
                    .buildToRawHttpMessage();
            BufferedReader br = new BufferedReader(new StringReader(rawHttpMessage));
            HttpRequest httpRequest = HttpRequest.parse(br);

            //when
            ModelAndView mav = articleHandler.getArticle(httpRequest);

            //then
            assertThat(mav.getView()).isEqualTo(ResourceUtils.getStaticFile("/article/index.html"));
            assertThat(mav.getStatusCode()).isEqualTo(HttpStatusCode.OK);
        }

        @Test
        @DisplayName("로그인 하지 않은 경우 로그인 폼 html을 반환한다.")
        void returnLoginFormWhenNoLogin() {
            //given
            User user = UserFixture.user();
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/article")
                    .buildToRawHttpMessage();
            BufferedReader br = new BufferedReader(new StringReader(rawHttpMessage));
            HttpRequest httpRequest = HttpRequest.parse(br);

            //when
            ModelAndView mav = articleHandler.getArticle(httpRequest);

            //then
            assertThat(mav.getView()).isEmpty();
            assertThat(mav.getStatusCode()).isEqualTo(HttpStatusCode.MOVED_PERMANENTLY);
            assertThat(mav.getHeaders().get("Location")).isNotEmpty().isEqualTo("/login");
        }
    }
}
