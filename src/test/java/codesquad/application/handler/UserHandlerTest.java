package codesquad.application.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.application.handler.UserHandler;
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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserHandlerTest {

    private Database database;
    private UserHandler userHandler;
    private SessionStorage sessionStorage;

    @BeforeEach
    void setUp() {
        database = new Database();
        sessionStorage = new SessionStorage();
        userHandler = new UserHandler(database, sessionStorage);
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
                    .build();
            BufferedReader br = new BufferedReader(new StringReader(rawHttpMessage));
            httpRequest = HttpRequest.parse(br);
        }

        @Test
        @DisplayName("새로운 유저가 생성된다.")
        void createUser() {
            //given

            //when
            ModelAndView mav = userHandler.createUser(httpRequest);

            //then
            Optional<User> optionalUser = database.findUserByUserId(mav.getModelValue("userId"));
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
            database.addUser(user);
            String sessionId = sessionStorage.store(user);
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/user/list")
                    .cookie("SID", sessionId)
                    .build();
            loginUserHttpRequest = HttpRequest.parse(new BufferedReader(new StringReader(rawHttpMessage)));
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
                    .build();
            HttpRequest httpRequest = HttpRequest.parse(new BufferedReader(new StringReader(rawHttpMessage)));

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