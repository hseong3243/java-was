package codesquad.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import codesquad.fixture.HttpFixture;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Nested
    @DisplayName("parse 호출 시")
    class ParseTest {

        @Test
        @DisplayName("입력으로 주어진 HTTP GET 메시지를 파싱한다.")
        void parseInputMessage() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET)
                    .path("/index.html")
                    .header("Host", "localhost:8080")
                    .header("Connection", "keep-alive")
                    .header("Cache-Control", "max-age=0")
                    .build();

            //when
            HttpRequest httpRequest = HttpRequest.parse(rawHttpMessage);

            //then
            assertThat(httpRequest.method()).isEqualTo(HttpMethod.GET);
            assertThat(httpRequest.requestUrl()).isEqualTo("/index.html");
            assertThat(httpRequest.httpVersion()).isEqualTo("HTTP/1.1");
            assertThat(httpRequest.header().get("Host")).isEqualTo("localhost:8080");
            assertThat(httpRequest.header().get("Connection")).isEqualTo("keep-alive");
            assertThat(httpRequest.header().get("Cache-Control")).isEqualTo("max-age=0");
        }

        @Test
        @DisplayName("HTTP GET 메시지로부터 쿼리 스트링을 파싱한다.")
        void parseQueryString() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET)
                    .path("/create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net")
                    .header("Host", "localhost:8080")
                    .header("Connection", "keep-alive")
                    .header("Cache-Control", "max-age=0")
                    .build();

            //when
            HttpRequest httpRequest = HttpRequest.parse(rawHttpMessage);

            //then
            assertThat(httpRequest.method()).isEqualTo(HttpMethod.GET);
            assertThat(httpRequest.requestUrl()).isEqualTo("/create");
            assertThat(httpRequest.httpVersion()).isEqualTo("HTTP/1.1");
            assertThat(httpRequest.header().get("Host")).isEqualTo("localhost:8080");
            assertThat(httpRequest.header().get("Connection")).isEqualTo("keep-alive");
            assertThat(httpRequest.header().get("Cache-Control")).isEqualTo("max-age=0");
            Map<String, String> queries = httpRequest.queries();
            assertThat(queries.get("userId")).isNotNull().isEqualTo("javajigi");
            assertThat(queries.get("password")).isNotNull().isEqualTo("password");
            assertThat(queries.get("name")).isNotNull().isEqualTo("박재성");
            assertThat(queries.get("email")).isNotNull().isEqualTo("javajigi@slipp.net");
        }

        @Test
        @DisplayName("?만 들어오면 무시한다.")
        void ignorePathHasQuestionMark() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET)
                    .path("/create?")
                    .header("Host", "localhost:8080")
                    .header("Connection", "keep-alive")
                    .header("Cache-Control", "max-age=0")
                    .build();

            //when
            HttpRequest httpRequest = HttpRequest.parse(rawHttpMessage);

            //then
            assertThat(httpRequest.method()).isEqualTo(HttpMethod.GET);
            assertThat(httpRequest.requestUrl()).isEqualTo("/create");
            assertThat(httpRequest.httpVersion()).isEqualTo("HTTP/1.1");
            assertThat(httpRequest.header().get("Host")).isEqualTo("localhost:8080");
            assertThat(httpRequest.header().get("Connection")).isEqualTo("keep-alive");
            assertThat(httpRequest.header().get("Cache-Control")).isEqualTo("max-age=0");
        }

        @Test
        @DisplayName("예외(IllegalArgument): 쿼리 파라미터에 =가 없으면")
        void illegalArgument_WhenQueryDoesNotHaveEqual() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET)
                    .path("/create?key")
                    .header("Host", "localhost:8080")
                    .header("Connection", "keep-alive")
                    .header("Cache-Control", "max-age=0")
                    .build();

            //when
            Exception exception = catchException(() -> HttpRequest.parse(rawHttpMessage));

            //then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("입력으로 주어진 HTTP POST 메시지를 파싱한다.")
        void parsePostMessage() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.POST)
                    .path("/user/create")
                    .header("Host", "localhost:8080")
                    .body("userId=userId&nickname=nickname")
                    .build();

            //when
            HttpRequest httpRequest = HttpRequest.parse(rawHttpMessage);

            //then
            assertThat(httpRequest.httpBody().data()).satisfies(data -> {
                assertThat(data).hasSize(2);
                assertThat(data.get("userId")).isNotNull().isEqualTo("userId");
                assertThat(data.get("nickname")).isNotNull().isEqualTo("nickname");
            });
        }
    }
}
