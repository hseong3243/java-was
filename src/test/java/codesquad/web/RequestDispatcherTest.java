package codesquad.web;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.base.ApplicationTest;
import codesquad.fixture.HttpFixture;
import codesquad.message.HttpMethod;
import codesquad.message.HttpRequest;
import codesquad.message.HttpResponse;
import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RequestDispatcherTest extends ApplicationTest {

    @Nested
    @DisplayName("dispatch 호출 시")
    class DispatchTest {

        @Test
        @DisplayName("요청 경로에 맞는 응답을 생성한다.")
        void createHttpResponse() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.POST).path("/user/create")
                    .body("userId=userId&password=password&name=name&email=email@email.com")
                    .build();
            BufferedReader br = new BufferedReader(new StringReader(rawHttpMessage));
            HttpRequest httpRequest = HttpRequest.parse(br);

            //when
            HttpResponse httpResponse = requestDispatcher.dispatch(httpRequest);

            //then
            String httpStartLine = new String(httpResponse.getHttpMessageStartLine());
            assertThat(httpStartLine).isEqualTo("HTTP/1.1 302 Found\n");
        }
    }
}