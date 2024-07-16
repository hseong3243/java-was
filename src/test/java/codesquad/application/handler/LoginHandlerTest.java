package codesquad.application.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.application.database.UserMemoryDatabase;
import codesquad.application.database.SessionMemoryStorage;
import codesquad.application.util.ResourceUtils;
import codesquad.fixture.HttpFixture;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpStatusCode;
import codesquad.application.model.User;
import codesquad.application.web.ModelAndView;
import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    private LoginHandler loginHandler;
    private UserMemoryDatabase userMemoryDatabase;
    private SessionMemoryStorage sessionStorage;

    @BeforeEach
    void setUp() {
        userMemoryDatabase = new UserMemoryDatabase();
        sessionStorage = new SessionMemoryStorage();
        loginHandler = new LoginHandler(userMemoryDatabase, sessionStorage);
    }

    @Nested
    @DisplayName("getLoginForm 호출 시")
    class GetLoginFormTest {
        @Test
        @DisplayName("로그인 폼을 반환한다.")
        void returnLoginForm() {
            //given
            HttpRequest httpRequest = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/login")
                    .buildToHttpRequest();

            //when
            ModelAndView modelAndView = loginHandler.getLoginForm(httpRequest);

            //then
            assertThat(modelAndView.getView()).isEqualTo(ResourceUtils.getStaticFile("/login/index.html"));
        }
    }

    @Nested
    @DisplayName("login 호출 시")
    class LoginTest {

        private HttpRequest httpRequest;
        private User user;

        @BeforeEach
        void setUp() {
            httpRequest = HttpFixture.builder()
                    .method(HttpMethod.POST)
                    .path("/login")
                    .body("userId=userId&password=password")
                    .buildToHttpRequest();
            user = User.create("userId", "password", "name", "email@email.com");
            userMemoryDatabase.addUser(user);
        }

        @Test
        @DisplayName("로그인에 성공하면 메인 페이지로 리다이렉트한다.")
        void redirectToMain() {
            //given

            //when
            ModelAndView mav = loginHandler.login(httpRequest);

            //then
            assertThat(mav.getStatusCode()).isEqualTo(HttpStatusCode.FOUND);
            assertThat(mav.getHeaders()).satisfies(headers -> {
                assertThat(headers.get("Location")).isNotNull().isEqualTo("/");
            });
        }

        @Test
        @DisplayName("로그인에 성공하면 쿠키를 반환한다.")
        void returnLoginCookie() {
            //given

            //when
            ModelAndView mav = loginHandler.login(httpRequest);

            //then
            assertThat(mav.getHeaders()).satisfies(headers -> {
                assertThat(headers.get("Set-Cookie")).isNotNull()
                        .contains("SID=")
                        .contains("Path=/")
                        .contains("HttpOnly");
            });
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 로그인 실패 페이지로 리다이렉트한다.")
        void noSuchElementEx_WhenUserNotFound() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.POST)
                    .path("/login")
                    .body("userId=nope&password=nope")
                    .buildToRawHttpMessage();
            BufferedReader br = new BufferedReader(new StringReader(rawHttpMessage));
            HttpRequest httpRequest = HttpRequest.parse(br);

            //when
            ModelAndView mav = loginHandler.login(httpRequest);

            //then
            assertThat(mav.getHeaders()).satisfies(headers -> {
                assertThat(headers.get("Set-Cookie")).isNull();
                assertThat(headers.get("Location")).isNotNull().isEqualTo("/login/failed.html");
            });
        }

        @Test
        @DisplayName("유저의 패스워드와 일치하지 않으면 로그인 실패 페이지로 리다이렉트한다.")
        void test() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.POST)
                    .path("/login")
                    .body("userId=userId&password=nope")
                    .buildToRawHttpMessage();
            BufferedReader br = new BufferedReader(new StringReader(rawHttpMessage));
            HttpRequest httpRequest = HttpRequest.parse(br);

            //when
            ModelAndView mav = loginHandler.login(httpRequest);

            //then
            assertThat(mav.getHeaders()).satisfies(headers -> {
                assertThat(headers.get("Set-Cookie")).isNull();
                assertThat(headers.get("Location")).isNotNull().isEqualTo("/login/failed.html");
            });
        }
    }
}
