package codesquad.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Nested
    @DisplayName("getHttpMessageBody 호출 시")
    class GetHttpMessageBody {

        @Test
        @DisplayName("HTTP 메시지 바디가 반환된다.")
        void getBody() {
            //given
            HttpResponse httpResponse = new HttpResponse("HTTP/1.1", HttpStatusCode.OK, "바디 내용");
            httpResponse.addHeader("Content-Type", "text/html");

            //when
            byte[] httpMessageBody = httpResponse.getHttpMessageBody();

            //then
            assertThat(new String(httpMessageBody)).isEqualTo("바디 내용");
        }
    }

    @Nested
    @DisplayName("getHttpMessageHeaders 호출 시")
    class GetHttpMessageHeaders {

        @Test
        @DisplayName("HTTP 메시지 헤더가 반환된다.")
        void getHeaders() {
            //given
            HttpResponse httpResponse = new HttpResponse("HTTP/1.1", HttpStatusCode.OK, "바디 내용");
            httpResponse.addHeader("Content-Type", "text/html");
            httpResponse.addHeader("Content-Length", "10");

            //when
            byte[] httpMessageHeaders = httpResponse.getHttpMessageHeaders();

            //then
            assertThat(httpMessageHeaders).asString()
                    .isEqualTo("""
                            Content-Length: 10
                            Content-Type: text/html
                            """);
        }
    }

    @Nested
    @DisplayName("getHttpMessageStartLine 호출 시")
    class GetHttpMessageStartLine {

        @Test
        @DisplayName("HTTP 메시지 개행이 반환된다.")
        void getStartLine() {
            //given
            HttpResponse httpResponse = new HttpResponse("HTTP/1.1", HttpStatusCode.OK, "바디 내용");

            //when
            byte[] httpMessageStartLine = httpResponse.getHttpMessageStartLine();

            //then
            assertThat(httpMessageStartLine).asString()
                    .isEqualTo("HTTP/1.1 200 OK" + "\n");
        }
    }
}
