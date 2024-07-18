package codesquad.server.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpHeadersTest {

    @Nested
    @DisplayName("parse 호출 시")
    class ParseTest {

        @Test
        @DisplayName("파싱한 객체를 반환한다.")
        void parseRawHttpHeaders() throws IOException {
            //given
            String rawHeaders = """
                    Accept: application/json\r
                    Authorization: Bearer accessToken\r
                    Host: localhost:8080\r
                    Connection: keep-alive\r
                    Accept-Language: en-US,en;q=0.5\r
                    Accept-Encoding: gzip, deflate\r
                    \r\n
                    """;
            ByteArrayInputStream clientInput = new ByteArrayInputStream(rawHeaders.getBytes());

            //when
            HttpHeaders httpHeaders = HttpHeaders.parse(clientInput);

            //then
            assertThat(httpHeaders.headers()).satisfies(headers -> {
                assertThat(headers).hasSize(6);
                assertThat(headers.get("Authorization")).isNotNull().isEqualTo("Bearer accessToken");
                assertThat(headers.get("Accept")).isNotNull().isEqualTo("application/json");
                assertThat(headers.get("Host")).isNotNull().isEqualTo("localhost:8080");
                assertThat(headers.get("Connection")).isNotNull().isEqualTo("keep-alive");
                assertThat(headers.get("Accept-Language")).isNotNull().isEqualTo("en-US,en;q=0.5");
                assertThat(headers.get("Accept-Encoding")).isNotNull().isEqualTo("gzip, deflate");
            });
        }

        @ParameterizedTest
        @CsvSource({
                "Host: ",
                "Key:value",
                ": value"
        })
        @DisplayName("예외(IllegalArgument): 형식이 올바르지 않으면")
        void illegalArgument_WhenInvalidFormat(String rawHeaders) {
            //given
            ByteArrayInputStream clientInput = new ByteArrayInputStream(rawHeaders.getBytes());

            //whenrr
            Exception exception = catchException(() -> HttpHeaders.parse(clientInput));

            //then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }

}