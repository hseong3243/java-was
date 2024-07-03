package codesquad.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
            String rawHttpMessage = """
                    GET /index.html HTTP/1.1
                    Host: localhost:8080
                    Connection: keep-alive
                    Cache-Control: max-age=0""";

            //when
            HttpRequest httpRequest = HttpRequest.parse(rawHttpMessage);

            //then
            assertEquals("GET", httpRequest.method());
            assertEquals("/index.html", httpRequest.requestUrl());
            assertEquals("HTTP/1.1", httpRequest.httpVersion());
            assertEquals("localhost:8080", httpRequest.header().get("Host"));
            assertEquals("keep-alive", httpRequest.header().get("Connection"));
            assertEquals("max-age=0", httpRequest.header().get("Cache-Control"));
        }

        @Test
        @DisplayName("HTTP GET 메시지로부터 쿼리 스트링을 파싱한다.")
        void parseQueryString() {
            //given
            String rawHttpMessage = """
                    GET /create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net HTTP/1.1
                    Host: localhost:8080
                    Connection: keep-alive
                    Cache-Control: max-age=0""";

            //when
            HttpRequest httpRequest = HttpRequest.parse(rawHttpMessage);

            //then
            Map<String, String> queries = httpRequest.queries();
            assertThat(queries.get("userId")).isNotNull().isEqualTo("javajigi");
            assertThat(queries.get("password")).isNotNull().isEqualTo("password");
            assertThat(queries.get("name")).isNotNull().isEqualTo("박재성");
            assertThat(queries.get("email")).isNotNull().isEqualTo("javajigi@slipp.net");
        }
    }
}
