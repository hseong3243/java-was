package codesquad.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.message.HttpRequest;
import codesquad.message.HttpStatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CreateUserHandlerTest {

    @Nested
    @DisplayName("handle 호출 시")
    class Handle {

        private CreateUserHandler createUserHandler;
        private String rawHttpMessage;

        @BeforeEach
        void setUp() {
            createUserHandler = new CreateUserHandler();
            rawHttpMessage = """
                    POST /create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net HTTP/1.1
                    Host: localhost:8080
                    Connection: keep-alive
                    Cache-Control: max-age=0""";
        }

        @Test
        @DisplayName("사용자가 생성된다.")
        void userCreate() {
            //given
            HttpRequest httpRequest = HttpRequest.parse(rawHttpMessage);

            //when
            ModelAndView mav = createUserHandler.handle(httpRequest);

            //then
            assertThat(mav.getModelValue("userId")).isNotNull().isEqualTo("javajigi");

        }

        @Test
        @DisplayName("/index.html 페이지로 리다이렉트한다.")
        void redirectToIndexPage() {
            //given
            HttpRequest httpRequest = HttpRequest.parse(rawHttpMessage);

            //when
            ModelAndView mav = createUserHandler.handle(httpRequest);

            //then
            assertThat(mav.getHeaders().get("Location")).isNotNull().isEqualTo("/");
            assertThat(mav.getStatusCode()).isEqualTo(HttpStatusCode.FOUND);
        }
    }
}
