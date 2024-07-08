package codesquad.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

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
        void parseRawHttpHeaders() {
            //given
            String rawHeaders = """
                    Accept: application/json
                    Authorization: Bearer accessToken
                    Host: localhost:8080
                    Connection: keep-alive
                    Accept-Language: en-US,en;q=0.5
                    Accept-Encoding: gzip, deflate""";

            //when
            HttpHeaders httpHeaders = HttpHeaders.parse(rawHeaders);

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
            //when
            Exception exception = catchException(() -> HttpHeaders.parse(rawHeaders));

            //then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }

}