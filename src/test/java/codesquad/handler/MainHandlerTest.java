package codesquad.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.database.Database;
import codesquad.database.SessionStorage;
import codesquad.fixture.HttpFixture;
import codesquad.fixture.UserFixture;
import codesquad.message.HttpMethod;
import codesquad.message.HttpRequest;
import codesquad.message.HttpStatusCode;
import codesquad.model.User;
import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MainHandlerTest {

    private MainHandler mainHandler;
    private Database database;
    private SessionStorage sessionStorage;
    private HttpRequest httpRequest;
    private User user;

    @BeforeEach
    void setUp() {
        database = new Database();
        sessionStorage = new SessionStorage();
        mainHandler = new MainHandler(database, sessionStorage);
        user = UserFixture.user();
        database.addUser(user);
        String sessionId = sessionStorage.store(user);
        String rawHttpMessage = HttpFixture.builder()
                .method(HttpMethod.GET).path("/")
                .cookie("SID", sessionId)
                .build();
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
                    .build();
            HttpRequest httpRequest = HttpRequest.parse(new BufferedReader(new StringReader(rawHttpMessage)));

            //when
            ModelAndView mav = mainHandler.mainPage(httpRequest);

            //then
            assertThat(mav.getModelValue("userId")).isNull();
            assertThat(mav.getModelValue("name")).isNull();
        }
    }
}