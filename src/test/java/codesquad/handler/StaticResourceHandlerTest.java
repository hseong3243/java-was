package codesquad.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.database.DataBase;
import codesquad.database.SessionStorage;
import codesquad.message.HttpBody;
import codesquad.message.HttpCookies;
import codesquad.message.HttpHeaders;
import codesquad.message.HttpRequest;
import codesquad.message.HttpStartLine;
import codesquad.model.User;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StaticResourceHandlerTest {

    @Nested
    @DisplayName("handle 호출 시")
    class HandleTest {

        private StaticResourceHandler staticResourceHandler;
        private String userId;
        private String name;
        private String password;
        private String email;
        private String sessionId;

        @BeforeEach
        void setUp() {
            userId = "staticUser";
            name = "static";
            password = "User";
            staticResourceHandler = new StaticResourceHandler();
            User user = User.create(userId, password, name, "");
            DataBase.addUser(user);
            sessionId = SessionStorage.store(user);
        }

        @Nested
        @DisplayName("경로가 html인 경우")
        class HtmlPath {

            private HttpStartLine httpStartLine;
            private HttpHeaders httpHeaders;
            private HttpBody httpBody;
            private HttpCookies httpCookies;
            private HttpRequest httpRequest;
            private Map<String, String> headers;

            @BeforeEach
            void setUp() {
                httpStartLine = new HttpStartLine("GET", "/", new HashMap<>(), "HTTP/1.1");
                headers = new HashMap<>();
                httpHeaders = new HttpHeaders(headers);
                httpBody = new HttpBody(new HashMap<>());
                httpCookies = new HttpCookies(new HashMap<>());
                httpRequest = new HttpRequest(httpStartLine, httpHeaders, httpCookies, httpBody);
            }

            @Test
            @DisplayName("로그인한 사용자 정보를 추가한다.")
            void thenAddUserInfoToModel() {
                //given
                httpRequest.httpCookies().cookies().put("SID", sessionId);

                //when
                ModelAndView modelAndView = staticResourceHandler.handle(httpRequest);

                //then
                assertThat(modelAndView.getModelValue("userId")).isNotNull().isEqualTo(userId);
                assertThat(modelAndView.getModelValue("name")).isNotNull().isEqualTo(name);
                assertThat(modelAndView.getModelValue("password")).isNull();
            }

            @Test
            @DisplayName("로그인한 사용자 정보가 없는 경우 추가하지 않는다.")
            void thenDoesNotAddUserInfo() {
                //given
                headers.clear();

                //when
                ModelAndView modelAndView = staticResourceHandler.handle(httpRequest);

                //then
                assertThat(modelAndView.getModelValue("userId")).isNull();
                assertThat(modelAndView.getModelValue("name")).isNull();
                assertThat(modelAndView.getModelValue("userId")).isNull();
            }
        }

        @Nested
        @DisplayName("경로가 html이 아닌 경우")
        class NotHtmlPath {

            private HttpStartLine httpStartLine;
            private HttpHeaders httpHeaders;
            private HttpBody httpBody;
            private HttpCookies httpCookies;
            private HttpRequest httpRequest;
            private Map<String, String> headers;

            @BeforeEach
            void setUp() {
                httpStartLine = new HttpStartLine("GET", "/favicon.ico", new HashMap<>(), "HTTP/1.1");
                headers = new HashMap<>();
                httpHeaders = new HttpHeaders(headers);
                httpBody = new HttpBody(new HashMap<>());
                httpCookies = new HttpCookies(new HashMap<>());
                httpRequest = new HttpRequest(httpStartLine, httpHeaders, httpCookies, httpBody);
            }

            @Test
            @DisplayName("로그인한 사용자 정보를 추가하지 않는다.")
            void test() {
                //given
                headers.put("SID", sessionId);

                //when
                ModelAndView modelAndView = staticResourceHandler.handle(httpRequest);

                //then
                assertThat(modelAndView.getModelValue("userId")).isNull();
                assertThat(modelAndView.getModelValue("name")).isNull();
                assertThat(modelAndView.getModelValue("userId")).isNull();
            }
        }
    }
}
