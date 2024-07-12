package codesquad;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import codesquad.server.message.ContentTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ContentTypesTest {

    @Nested
    @DisplayName("컨텍츠 타입 조회 시")
    class GetContentTypes {

        @ParameterizedTest
        @CsvSource({
                "html, text/html",
                "css, text/css",
                "js, text/javascript",
                "jpg, image/jpeg",
                "png, image/png",
                "ico, image/x-icon",
                "svg, image/svg+xml"
        })
        @DisplayName("컨텐츠 타입을 반환한다.")
        void getContentTypes(String fileNameExtension, String contentType) {
            //given
            //when
            String result = ContentTypes.getMimeType(fileNameExtension);

            //then
            assertThat(result).isEqualTo(contentType);
        }

        @Test
        @DisplayName("예외(illegalArguemnt): 지원하지 않는 컨텐츠 타입")
        void exceptionWhenNoSupport() {
            //given
            String noSupport = "no/support";

            //when
            Exception exception = catchException(() -> ContentTypes.getMimeType(noSupport));

            //then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }
}