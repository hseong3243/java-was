package codesquad.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.database.DataBase;
import codesquad.database.SessionStorage;
import codesquad.fixture.UserFixture;
import codesquad.message.HttpBody;
import codesquad.message.HttpCookies;
import codesquad.message.HttpHeaders;
import codesquad.message.HttpRequest;
import codesquad.message.HttpStartLine;
import codesquad.message.HttpStatusCode;
import codesquad.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ListUserHandlerTest {

    @Nested
    @DisplayName("")
    class HandleTest {

        private ListUserHandler listUserHandler;
        private HttpRequest httpRequest;
        private User user;
        private Map<String, String> cookies = new HashMap<>();

        @BeforeEach
        void setUp() {
            listUserHandler = new ListUserHandler();
            user = UserFixture.user();
            DataBase.addUser(user);
            String sessionId = SessionStorage.store(user);

            HttpStartLine startLine = new HttpStartLine("GET", "/user/list", new HashMap<>(), "HTTP/1.1");
            HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>());
            cookies.put("SID", sessionId);
            HttpCookies httpCookies = new HttpCookies(cookies);
            HttpBody httpBody = new HttpBody(new HashMap<>());
            httpRequest = new HttpRequest(startLine, httpHeaders, httpCookies, httpBody);
        }

        @Test
        @DisplayName("사용자가 로그인 한 경우 사용자 목록을 출력한다.")
        void returnUserList_WhenLogin() {
            //given
            User user1 = User.create("test1", "pass1", "name1", "email1@email.com");
            User user2 = User.create("test2", "pass2", "name2", "email2@email.com");
            DataBase.addUser(user1);
            DataBase.addUser(user2);

            //when
            ModelAndView mav = listUserHandler.handle(httpRequest);

            //then
            List<User> users = DataBase.findAll();
            assertThat(mav.getModelValue("userList")).isNotNull()
                    .contains(users.get(0).getName())
                    .contains(users.get(1).getName())
                    .contains(users.get(2).getName());
        }

        @Test
        @DisplayName("로그인 하지 않은 경우 로그인 페이지로 리다이렉트한다.")
        void redirectLoginPage_WhenNoLogin() {
            //given
            cookies.clear();

            //when
            ModelAndView mav = listUserHandler.handle(httpRequest);

            //then
            assertThat(mav.getStatusCode()).isEqualTo(HttpStatusCode.FOUND);
            assertThat(mav.getHeaders().get("Location")).isNotNull().isEqualTo("/login");
        }
    }
}
