package codesquad.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Nested
    @DisplayName("toHttpMessage 호출 시")
    class ToHttpMessage {

        @Test
        @DisplayName("HTTP 메시지로 변환된다.")
        void test() {
            //given
            HttpResponse httpResponse = new HttpResponse("HTTP/1.1", 200, "OK", "body");
            httpResponse.addHeader("Content-Type", "text/html");

            //when
            String result = httpResponse.toHttpMessage();

            //then
            assertThat(result).isEqualTo("""
                    HTTP/1.1 200 OK
                    Content-Type: text/html
                                        
                    body""");
        }
    }
}
