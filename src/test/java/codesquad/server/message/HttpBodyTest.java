package codesquad.server.message;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.server.utils.MultiPartParser;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
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
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(rawBody.getBytes()));

            //when
            HttpBody httpBody = HttpBody.parse(bis, httpHeaders);

            //then
            assertThat(httpBody.data().get("data")).isNotNull().isBlank();
        }

        @Nested
        @DisplayName("컨텐츠 타입이 multipart/form-data 인 경우")
        class MultiPartTest {

            private String body;
            private HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>());

            @BeforeEach
            void setUp() {
                body = """
                    ------WebKitFormBoundaryCxwg8SiBWAa9oImg\r
                    Content-Disposition: form-data; name="title"\r
                    \r
                    qwer\r
                    ------WebKitFormBoundaryCxwg8SiBWAa9oImg\r
                    Content-Disposition: form-data; name="content"\r
                    \r
                    qwer\r
                    ------WebKitFormBoundaryCxwg8SiBWAa9oImg\r
                    Content-Disposition: form-data; name="image"; filename="carbon.png"\r
                    Content-Type: image/png\r
                    \r
                    asdf\r
                    ------WebKitFormBoundaryCxwg8SiBWAa9oImg--\r""";
                httpHeaders.headers().put("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryCxwg8SiBWAa9oImg");
                httpHeaders.headers().put("Content-Length", String.valueOf(body.getBytes().length));
            }

            @Test
            @DisplayName("파일 데이터를 파싱한다.")
            void parseFileData() throws IOException {
                //given
                BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(body.getBytes()));

                //when
                HttpBody httpBody = MultiPartParser.parse(httpHeaders, bis);

                //then
                assertThat(httpBody.files()).satisfies(files -> {
                    assertThat(files).containsKey("image");
                    assertThat(files.get("image")).satisfies(httpFile -> {
                        assertThat(httpFile.getContent()).isEqualTo("asdf".getBytes());
                        assertThat(httpFile.getContentType()).isEqualTo("image/png");
                        assertThat(httpFile.getFileName()).isEqualTo("carbon.png");
                    });
                });
            }

            @Test
            @DisplayName("파라미터를 파싱한다.")
            void parseParameters() throws IOException {
                //given
                BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(body.getBytes()));

                //when
                HttpBody httpBody = MultiPartParser.parse(httpHeaders, bis);

                //then
                assertThat(httpBody.data()).satisfies(data -> {
                    assertThat(data.get("title")).isNotNull().isEqualTo("qwer");
                    assertThat(data.get("content")).isNotNull().isEqualTo("qwer");
                });
            }
        }


    }
}
