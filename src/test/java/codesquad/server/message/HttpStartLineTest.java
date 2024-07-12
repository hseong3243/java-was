package codesquad.server.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpStartLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpStartLineTest {

    @Nested
    @DisplayName("parse 호출 시")
    class ParseTest {

        @Test
        @DisplayName("파싱한 객체를 반환한다.")
        void parseRawHttpStartLine() {
            //given
            String rawHttpStartLine = "GET /user/create?key=value&key2=value2 HTTP/1.1";

            //when
            HttpStartLine result = HttpStartLine.parse(rawHttpStartLine);

            //then
            assertThat(result.method()).isEqualTo(HttpMethod.GET);
            assertThat(result.path()).isEqualTo("/user/create");
            assertThat(result.version()).isEqualTo("HTTP/1.1");
            assertThat(result.queries()).satisfies(query -> {
                assertThat(query.get("key")).isNotNull().isEqualTo("value");
                assertThat(query.get("key2")).isNotNull().isEqualTo("value2");
            });
        }

        @ParameterizedTest
        @CsvSource({
                "GET /user/create?key HTTP/1.1",
                "GET /user&key=value"
        })
        @DisplayName("예외(IllegalArgument): 형식에 맞지 않으면")
        void illegalArgument_WhenNotSuitable(String rawHttpStartLine) {
            //given
            //when
            Exception exception = catchException(() -> HttpStartLine.parse(rawHttpStartLine));

            //then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }
}