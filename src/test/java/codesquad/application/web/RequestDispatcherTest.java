package codesquad.application.web;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.base.ApplicationTest;
import codesquad.fixture.HttpFixture;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpResponse;
import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RequestDispatcherTest extends ApplicationTest {

    @Nested
    @DisplayName("dispatch 호출 시")
    class DispatchTest {

        @Test
        @DisplayName("요청 경로에 맞는 응답을 생성한다.")
        @Disabled
        void createHttpResponse() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.POST).path("/user/create")
                    .body("userId=userId&password=password&name=name&email=email@email.com")
                    .buildToRawHttpMessage();
            BufferedReader br = new BufferedReader(new StringReader(rawHttpMessage));
            HttpRequest httpRequest = HttpRequest.parse(br);

            //when
            HttpResponse httpResponse = requestDispatcher.handle(httpRequest);

            //then
            String httpStartLine = new String(httpResponse.getHttpMessageStartLine());
            assertThat(httpStartLine).isEqualTo("HTTP/1.1 302 Found\n");
        }

        @Nested
        @DisplayName("예외가 발생하면")
        class WhenThrowExceptionTest {

            @Test
            @DisplayName("methodNotAllowed이면 allow 헤더를 추가하고 405 예외 페이지를 반환한다.")
            void methodNotAllowed_ThenReturn405Page() {
                //given
                HttpRequest httpRequest = HttpFixture.builder()
                        .method(HttpMethod.POST).path("/")
                        .buildToHttpRequest();

                //when
                HttpResponse response = requestDispatcher.handle(httpRequest);

                //then
                assertThat(response.getHttpMessageStartLine()).asString().contains("405");
                assertThat(response.getHttpMessageHeaders()).asString().contains("Allow: GET");
                assertThat(response.getHttpMessageBody()).asString().contains("405 Method Not Allowed");
            }
        }
    }
}