package codesquad.application.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.application.database.Database;
import codesquad.application.database.SessionStorage;
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

    @Nested
    @DisplayName("handle 호출 시")
    class HandleTest {

        private LoginHandler loginHandler;
        private Database database;
        private SessionStorage sessionStorage;
        private HttpRequest httpRequest;
        private User user;

        @BeforeEach
        void setUp() {
            database = new Database();
            sessionStorage = new SessionStorage();
            loginHandler = new LoginHandler(database, sessionStorage);
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.POST)
                    .path("/login")
                    .body("userId=userId&password=password")
                    .buildToRawHttpMessage();
            BufferedReader br = new BufferedReader(new StringReader(rawHttpMessage));
            httpRequest = HttpRequest.parse(br);
            user = User.create("userId", "password", "name", "email@email.com");
            database.addUser(user);
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
