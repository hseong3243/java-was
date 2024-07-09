package codesquad.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.database.DataBase;
import codesquad.message.HttpRequest;
import codesquad.message.HttpStatusCode;
import codesquad.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    @Nested
    @DisplayName("handle 호출 시")
    class HandleTest {

        private LoginHandler loginHandler;
        private String rawHttpRequestMessage;

        @BeforeEach
        void setUp() {
            loginHandler = new LoginHandler();
            rawHttpRequestMessage = """
                    POST /login HTTP/1.1
                    Content-Type: application/x-www-form-urlencoded
                                        
                    userId=userId&password=password""";
            User user = User.create("userId", "password", "name", "email@email.com");
            DataBase.addUser(user);
        }

        @Test
        @DisplayName("로그인에 성공하면 메인 페이지로 리다이렉트한다.")
        void redirectToMain() {
            //given
            HttpRequest httpRequest = HttpRequest.parse(rawHttpRequestMessage);

            //when
            ModelAndView mav = loginHandler.handle(httpRequest);

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
            HttpRequest httpRequest = HttpRequest.parse(rawHttpRequestMessage);

            //when
            ModelAndView mav = loginHandler.handle(httpRequest);

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
            String rawHttpRequestMessage = """
                    POST /login HTTP/1.1
                    Content-Type: application/x-www-form-urlencoded
                                        
                    userId=nope&password=nope""";
            HttpRequest httpRequest = HttpRequest.parse(rawHttpRequestMessage);

            //when
            ModelAndView mav = loginHandler.handle(httpRequest);

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
            String rawHttpRequestMessage = """
                    POST /login HTTP/1.1
                    Content-Type: application/x-www-form-urlencoded
                                        
                    userId=userId&password=nope""";
            HttpRequest httpRequest = HttpRequest.parse(rawHttpRequestMessage);

            //when
            ModelAndView mav = loginHandler.handle(httpRequest);

            //then
            assertThat(mav.getHeaders()).satisfies(headers -> {
                assertThat(headers.get("Set-Cookie")).isNull();
                assertThat(headers.get("Location")).isNotNull().isEqualTo("/login/failed.html");
            });
        }
    }
}
