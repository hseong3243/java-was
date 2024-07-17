package codesquad.application.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.application.database.SessionMemoryStorage;
import codesquad.application.database.UserMemoryDatabase;
import codesquad.application.model.User;
import codesquad.application.web.ModelAndView;
import codesquad.fixture.HttpFixture;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpStatusCode;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserHandlerTest {

    private UserMemoryDatabase userMemoryDatabase;
    private UserHandler userHandler;
    private SessionMemoryStorage sessionStorage;

    @BeforeEach
    void setUp() {
        userMemoryDatabase = new UserMemoryDatabase();
        sessionStorage = new SessionMemoryStorage();
        userHandler = new UserHandler(userMemoryDatabase, sessionStorage);
    }

    @Nested
    @DisplayName("createUser 호출 시")
    public class CreateUserTest {

        private HttpRequest httpRequest;

        @BeforeEach
        void setUp() {
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.POST).path("/user/create")
                    .body("userId=userId&password=password&name=name&email=email")
                    .buildToRawHttpMessage();
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(rawHttpMessage.getBytes()));
            httpRequest = HttpRequest.parse(bis);
        }

        @Test
        @DisplayName("새로운 유저가 생성된다.")
        void createUser() {
            //given

            //when
            ModelAndView mav = userHandler.createUser(httpRequest);

            //then
            Optional<User> optionalUser = userMemoryDatabase.findUserByUserId(mav.getModelValue("userId"));
            assertThat(optionalUser).isNotEmpty().get()
                    .satisfies(user -> {
                        assertThat(user.getUserId()).isEqualTo("userId");
                        assertThat(user.getPassword()).isEqualTo("password");
                        assertThat(user.getName()).isEqualTo("name");
                    });
        }

        @Test
        @DisplayName("메인 페이지로 리다이렉트 한다.")
        void redirectMain() {
            //given

            //when
            ModelAndView mav = userHandler.createUser(httpRequest);

            //then
            assertThat(mav.getStatusCode()).isEqualTo(HttpStatusCode.FOUND);
            assertThat(mav.getHeaders()).satisfies(headers -> {
                assertThat(headers.get("Location")).isNotNull().isEqualTo("/");
            });
        }
    }

    @Nested
    @DisplayName("listUser 호출 시")
    class ListUserTest {

        private HttpRequest loginUserHttpRequest;

        @BeforeEach
        void setUp() {
            User user = User.create("userId", "password", "name", "email@email.com");
            userMemoryDatabase.addUser(user);
            String sessionId = sessionStorage.store(user);
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/user/list")
                    .cookie("SID", sessionId)
                    .buildToRawHttpMessage();
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(rawHttpMessage.getBytes()));
            loginUserHttpRequest = HttpRequest.parse(bis);
        }

        @Test
        @DisplayName("사용자가 로그인 한 경우 유저 목록을 모델에 담는다.")
        void listUser() {
            //given

            //when
            ModelAndView mav = userHandler.listUser(loginUserHttpRequest);

            //then
            assertThat(mav.getModelValue("userList")).isNotNull()
                    .contains("UserId=userId", "Name=name");
        }

        @Test
        @DisplayName("사용자가 로그인하지 않은 경우 로그인 화면으로 리다이렉트한다.")
        void redirectToLogin_WhenNoLogin() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/user/list")
                    .buildToRawHttpMessage();
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(rawHttpMessage.getBytes()));
            HttpRequest httpRequest = HttpRequest.parse(bis);

            //when
            ModelAndView mav = userHandler.listUser(httpRequest);

            //then
            assertThat(mav.getStatusCode()).isEqualTo(HttpStatusCode.FOUND);
            assertThat(mav.getHeaders()).satisfies(headers -> {
                assertThat(headers.get("Location")).isNotNull().isEqualTo("/login");
            });
        }
    }
}