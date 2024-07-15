package codesquad.application.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.application.database.UserDatabase;
import codesquad.application.database.SessionStorage;
import codesquad.fixture.HttpFixture;
import codesquad.fixture.UserFixture;
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

class MainHandlerTest {

    private MainHandler mainHandler;
    private UserDatabase userDatabase;
    private SessionStorage sessionStorage;
    private HttpRequest httpRequest;
    private User user;

    @BeforeEach
    void setUp() {
        userDatabase = new UserDatabase();
        sessionStorage = new SessionStorage();
        mainHandler = new MainHandler(userDatabase, sessionStorage);
        user = UserFixture.user();
        userDatabase.addUser(user);
        String sessionId = sessionStorage.store(user);
        String rawHttpMessage = HttpFixture.builder()
                .method(HttpMethod.GET).path("/")
                .cookie("SID", sessionId)
                .buildToRawHttpMessage();
        httpRequest = HttpRequest.parse(new BufferedReader(new StringReader(rawHttpMessage)));
    }

    @Nested
    @DisplayName("mainPage 호출 시")
    class MainPage {

        @Test
        @DisplayName("메인 페이지를 반환한다.")
        void returnMain() {
            //given

            //when
            ModelAndView mav = mainHandler.mainPage(httpRequest);

            //then
            assertThat(mav.getStatusCode()).isEqualTo(HttpStatusCode.OK);
            assertThat(mav.getView()).isNotNull();
        }

        @Test
        @DisplayName("사용자가 로그인한 경우 모델에 사용자 정보를 담는다.")
        void addUserInfoToModel_WhenLogin() {
            //given

            //when
            ModelAndView mav = mainHandler.mainPage(httpRequest);

            //then
            assertThat(mav.getModelValue("userId")).isEqualTo(user.getUserId());
            assertThat(mav.getModelValue("name")).isEqualTo(user.getName());
        }

        @Test
        @DisplayName("사용자가 로그인하지 않은 경우 모델에 사용자 정보를 담지 않는다.")
        void test() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/")
                    .buildToRawHttpMessage();
            HttpRequest httpRequest = HttpRequest.parse(new BufferedReader(new StringReader(rawHttpMessage)));

            //when
            ModelAndView mav = mainHandler.mainPage(httpRequest);

            //then
            assertThat(mav.getModelValue("userId")).isNull();
            assertThat(mav.getModelValue("name")).isNull();
        }
    }
}