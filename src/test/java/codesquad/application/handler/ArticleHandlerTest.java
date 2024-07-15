package codesquad.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import codesquad.application.database.ArticleDatabase;
import codesquad.application.database.SessionStorage;
import codesquad.application.database.UserDatabase;
import codesquad.application.model.Article;
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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ArticleHandlerTest {

    private ArticleHandler articleHandler;
    private SessionStorage sessionStorage;
    private ArticleDatabase articleDatabase;
    private UserDatabase userDatabase;

    @BeforeEach
    void setUp() {
        sessionStorage = new SessionStorage();
        articleDatabase = new ArticleDatabase();
        userDatabase = new UserDatabase();
        articleHandler = new ArticleHandler(articleDatabase, sessionStorage, userDatabase);
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

    @Nested
    @DisplayName("postArticle 호출 시")
    class PostArticleTest {

        @Test
        @DisplayName("로그인한 유저는 게시글이 생성된다.")
        void createArticle() {
            //given
            User user = UserFixture.user();
            String sessionId = sessionStorage.store(user);
            userDatabase.addUser(user);
            HttpRequest httpRequest = HttpFixture.builder()
                    .method(HttpMethod.POST).path("/article")
                    .cookie("SID", sessionId)
                    .body("title=title&content=content")
                    .buildToHttpRequest();

            //when
            ModelAndView mav = articleHandler.postArticle(httpRequest);

            //then
            assertThat(mav.getModelValue("articleId")).isNotNull().asLong().isEqualTo(1);
            Optional<Article> optionalArticle = articleDatabase.findById(1L);
            assertThat(optionalArticle).isNotEmpty().get()
                    .satisfies(article -> {
                        assertThat(article.getTitle()).isEqualTo("title");
                        assertThat(article.getContent()).isEqualTo("content");
                        assertThat(article.getAuthor().getUserId()).isEqualTo(user.getUserId());
                        assertThat(article.getAuthor().getName()).isEqualTo(user.getName());
                    });
        }

        @Test
        @DisplayName("로그인하지 않은 경우 로그인 화면으로 리다이렉트 된다.")
        void redirectToLoginWhenNoLogin() {
            HttpRequest httpRequest = HttpFixture.builder()
                    .method(HttpMethod.POST).path("/article")
                    .body("title=제목&content=내용")
                    .buildToHttpRequest();

            //when
            ModelAndView mav = articleHandler.postArticle(httpRequest);

            //then
            assertThat(mav.getView()).isEmpty();
            assertThat(mav.getStatusCode()).isEqualTo(HttpStatusCode.MOVED_PERMANENTLY);
            assertThat(mav.getHeaders().get("Location")).isNotEmpty().isEqualTo("/login");
        }

        @Test
        @DisplayName("예외(IllegalArgument): 게시글 제목이 null 이거나 공백인 경우")
        void illegalArgument_WhenTitleIsBlank() {
            //given
            User user = UserFixture.user();
            String sessionId = sessionStorage.store(user);
            userDatabase.addUser(user);
            HttpRequest httpRequest = HttpFixture.builder()
                    .method(HttpMethod.POST).path("/article")
                    .cookie("SID", sessionId)
                    .body("title=&content=content")
                    .buildToHttpRequest();

            //when
            Exception exception = catchException(() -> articleHandler.postArticle(httpRequest));

            //then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("예외(IllegalArgument): 게시글 본문이 null 이거나 공백인 경우")
        void illegalArgument_WhenContentIsBlank() {
            //given
            User user = UserFixture.user();
            String sessionId = sessionStorage.store(user);
            userDatabase.addUser(user);
            HttpRequest httpRequest = HttpFixture.builder()
                    .method(HttpMethod.POST).path("/article")
                    .cookie("SID", sessionId)
                    .body("title=title&content=")
                    .buildToHttpRequest();

            //when
            Exception exception = catchException(() -> articleHandler.postArticle(httpRequest));

            //then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
