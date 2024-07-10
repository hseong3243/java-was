package codesquad.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import codesquad.fixture.HttpFixture;
import codesquad.message.HttpMethod;
import codesquad.message.HttpRequest;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HandlerMapperTest {

    @Nested
    @DisplayName("mapping 메서드 호출 시")
    class MappingTest {

        @Test
        @DisplayName("예외(NoSuchElement): POST 요청에 매핑되는 핸들러가 없으면 ")
        void noSuchElement_WhenNotMatchPostRequest() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.POST)
                    .path("/user")
                    .header("Accept", "application/json")
                    .build();
            BufferedReader br = new BufferedReader(new StringReader(rawHttpMessage));
            HttpRequest httpRequest = HttpRequest.parse(br);

            //when
            Exception exception = catchException(() -> HandlerMapper.mapping(httpRequest));

            //then
            assertThat(exception).isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("GET 요청에 매핑되는 핸들러가 없으면 정적 리소스 핸들러를 반환한다.")
        void test() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET)
                    .path("/user")
                    .header("Accept", "application/json")
                    .build();
            BufferedReader br = new BufferedReader(new StringReader(rawHttpMessage));
            HttpRequest httpRequest = HttpRequest.parse(br);

            //when
            Handler handler = HandlerMapper.mapping(httpRequest);

            //then
            assertThat(handler).isInstanceOf(StaticResourceHandler.class);
        }

        @ParameterizedTest
        @MethodSource("findHandler")
        @DisplayName("URL 경로에 매핑된 핸들러를 조회한다.")
        void findHandler(String url, Handler expected) {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.POST)
                    .path("/user/create")
                    .header("Accept", "application/json")
                    .build();
            BufferedReader br = new BufferedReader(new StringReader(rawHttpMessage));
            HttpRequest httpRequest = HttpRequest.parse(br);

            //when
            Handler handler = HandlerMapper.mapping(httpRequest);

            //then
            assertThat(handler).isInstanceOf(expected.getClass());
        }

        private static Stream<Arguments> findHandler() {
            return Stream.of(
                    Arguments.arguments("/user/create", new CreateUserHandler(),
                            Arguments.arguments("/login", new LoginHandler()))
            );
        }
    }
}
