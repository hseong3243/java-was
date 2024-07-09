package codesquad.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpCookiesTest {

    @Nested
    @DisplayName("parse 호출 시")
    class ParseTest {

        @Test
        @DisplayName("쿠키 헤더 문자열을 파싱한다.")
        void parse() {
            //given
            String cookieHeader = "Test=asdfasdfasdfasd-asdf; SID=f31c4a90-8477-477f-913d-edf73bd62409";

            //when
            HttpCookies httpCookies = HttpCookies.parse(cookieHeader);

            //then
            assertThat(httpCookies.cookies()).satisfies(cookies -> {
                assertThat(cookies).hasSize(2);
                assertThat(cookies.get("Test")).isNotNull().isEqualTo("asdfasdfasdfasd-asdf");
                assertThat(cookies.get("SID")).isNotNull().isEqualTo("f31c4a90-8477-477f-913d-edf73bd62409");
            });
        }

        @Test
        @DisplayName("공백이 들어오면 비어있는 값을 반환한다.")
        void returnEmpty() {
            //given
            String cookieHeader = "";

            //when
            HttpCookies httpCookies = HttpCookies.parse(cookieHeader);

            //then
            assertThat(httpCookies.cookies()).isEmpty();
        }
    }
}