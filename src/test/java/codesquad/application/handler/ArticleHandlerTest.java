package codesquad.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import codesquad.application.database.memory.ArticleMemoryDatabase;
import codesquad.application.file.ImageStore;
import codesquad.application.database.memory.SessionMemoryStorage;
import codesquad.application.database.memory.UserMemoryDatabase;
import codesquad.application.model.Article;
import codesquad.application.model.User;
import codesquad.application.util.ResourceUtils;
import codesquad.application.web.ModelAndView;
import codesquad.fixture.HttpFixture;
import codesquad.fixture.UserFixture;
import codesquad.server.message.HttpFile;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpStatusCode;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ArticleHandlerTest {

    private ArticleHandler articleHandler;
    private SessionMemoryStorage sessionStorage;
    private ArticleMemoryDatabase articleDatabase;
    private UserMemoryDatabase userMemoryDatabase;
    private ImageStore imageStore;

    @BeforeEach
    void setUp() {
        sessionStorage = new SessionMemoryStorage();
        articleDatabase = new ArticleMemoryDatabase();
        userMemoryDatabase = new UserMemoryDatabase();
        imageStore = new ImageStore();
        articleHandler = new ArticleHandler(articleDatabase, sessionStorage, userMemoryDatabase, imageStore);
    }

    @Nested
    @DisplayName("getArticleForm 호출 시")
    class GetArticleWriteTest {

        @Test
        @DisplayName("로그인한 경우 글쓰기 폼 html을 반환한다.")
        void returnArticleFormWhenUserLogin() {
            //given
            User user = UserFixture.user();
            String sessionId = sessionStorage.store(user);
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/article/write")
                    .cookie("SID", sessionId)
                    .buildToRawHttpMessage();
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(rawHttpMessage.getBytes()));
            HttpRequest httpRequest = HttpRequest.parse(bis);

            //when
            ModelAndView mav = articleHandler.getArticleForm(httpRequest);

            //then
            assertThat(mav.getView()).isEqualTo(ResourceUtils.getStaticFile("/article/write.html"));
            assertThat(mav.getStatusCode()).isEqualTo(HttpStatusCode.OK);
        }

        @Test
        @DisplayName("로그인 하지 않은 경우 로그인 폼 html을 반환한다.")
        void returnLoginFormWhenNoLogin() {
            //given
            User user = UserFixture.user();
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/article/write")
                    .buildToRawHttpMessage();
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(rawHttpMessage.getBytes()));
            HttpRequest httpRequest = HttpRequest.parse(bis);

            //when
            ModelAndView mav = articleHandler.getArticleForm(httpRequest);

            //then
            assertThat(mav.getView()).isEmpty();
            assertThat(mav.getStatusCode()).isEqualTo(HttpStatusCode.FOUND);
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
            userMemoryDatabase.addUser(user);
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
        @DisplayName("이미지가 존재하는 경우 이미지 경로를 함께 저장한다.")
        void ifImageExists_ThenStoreImagePath() {
            //given
            User user = UserFixture.user();
            userMemoryDatabase.addUser(user);
            HttpRequest httpRequest = HttpFixture.builder()
                    .method(HttpMethod.POST).path("/article")
                    .cookie("SID", sessionStorage.store(user))
                    .body("title=title&content=content")
                    .buildToHttpRequest();
            httpRequest.files().put("image", new HttpFile("asdf.png", "image/png", "file".getBytes()));

            //when
            ModelAndView mav = articleHandler.postArticle(httpRequest);

            //then
            assertThat(mav.getModelValue("articleId")).isNotNull().asLong().isEqualTo(1);
            Optional<Article> optionalArticle = articleDatabase.findById(1L);
            assertThat(optionalArticle).isNotEmpty().get()
                    .satisfies(article -> {
                        assertThat(article.getImageFilename()).isNotBlank();
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
            assertThat(mav.getStatusCode()).isEqualTo(HttpStatusCode.FOUND);
            assertThat(mav.getHeaders().get("Location")).isNotEmpty().isEqualTo("/login");
        }

        @Test
        @DisplayName("예외(IllegalArgument): 게시글 제목이 null 이거나 공백인 경우")
        void illegalArgument_WhenTitleIsBlank() {
            //given
            User user = UserFixture.user();
            String sessionId = sessionStorage.store(user);
            userMemoryDatabase.addUser(user);
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
            userMemoryDatabase.addUser(user);
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

    @Nested
    @DisplayName("getArticle 호출 시")
    class GetArticleTest {

        @Test
        @DisplayName("articleId에 해당하는 게시글을 반환한다.")
        void findArticle() {
            //given
            HttpRequest httpRequest = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/article?articleId=1")
                    .buildToHttpRequest();
            User user = UserFixture.user();
            Article article = Article.create(articleDatabase.getNextId(), "제목", "내용", user);
            articleDatabase.save(article);

            //when
            ModelAndView mav = articleHandler.getArticle(httpRequest);

            //then
            assertThat(mav.getModelValue("articleId")).isNotNull().asLong().isEqualTo(article.getArticleId());
            assertThat(mav.getModelValue("title")).isNotNull().isEqualTo(article.getTitle());
            assertThat(mav.getModelValue("content")).isNotNull().isEqualTo(article.getContent());
            assertThat(mav.getModelValue("userId")).isNotNull().isEqualTo(user.getUserId());
            assertThat(mav.getModelValue("username")).isNotNull().isEqualTo(user.getName());
        }

        @Test
        @DisplayName("예외(noSuchElement): articleId에 해당하는 게시글이 없으면")
        void noSuchElement_WhenNoArticle() {
            //given
            HttpRequest httpRequest = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/article?articleId=2")
                    .buildToHttpRequest();

            //when
            Exception exception = catchException(() -> articleHandler.getArticle(httpRequest));

            //then
            assertThat(exception).isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("예외(illegalArgument): articleId가 쿼리 파라미터에 없으면")
        void illegalArgument_WhenArticleIdIsEmpty() {
            //given
            HttpRequest httpRequest = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/article")
                    .buildToHttpRequest();

            //when
            Exception exception = catchException(() -> articleHandler.getArticle(httpRequest));

            //then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("getArticles 호출 시")
    class GetArticlesTest {

        @Test
        @DisplayName("게시글 목록을 모델에 담는다.")
        void test() {
            //given
            HttpRequest httpRequest = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/articles")
                    .buildToHttpRequest();

            //when
            ModelAndView mav = articleHandler.getArticles(httpRequest);

            //then
            assertThat(mav.get("articles")).isNotNull().isInstanceOf(List.class);
        }
    }
}
