package codesquad;

import static org.junit.jupiter.api.Assertions.*;

import codesquad.message.HttpRequest;
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
    }
}
