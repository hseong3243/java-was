package codesquad.server.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpBodyTest {

    @Nested
    @DisplayName("parse 호출 시")
    class ParseTest {

        @Test
        @DisplayName("값이 없는 경우 빈 문자열을 값으로 한다.")
        void test() throws IOException {
            //given
            String rawBody = "data=";
            BufferedReader br = new BufferedReader(new StringReader(rawBody));
            HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>());
            httpHeaders.headers().put("Content-Length", String.valueOf(rawBody.getBytes().length));
            httpHeaders.headers().put("Content-Type", "application/x-www-form-urlencoded");

            //when
            HttpBody httpBody = HttpBody.parse(br, httpHeaders);

            //then
            assertThat(httpBody.data().get("data")).isNotNull().isBlank();
        }
    }
}
