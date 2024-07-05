package codesquad.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HandlerMapperTest {

    @Nested
    @DisplayName("find 메서드 호출 시")
    class FindTest {

        @ParameterizedTest
        @MethodSource("findHandler")
        @DisplayName("URL 경로에 매핑된 핸들러를 조회한다.")
        void findHandler(String url, Handler expected) {
            //given
            //when
            Handler handler = HandlerMapper.mapping(url);

            //then
            assertThat(handler).isInstanceOf(expected.getClass());
        }

        private static Stream<Arguments> findHandler() {
            return Stream.of(
                    Arguments.arguments("/user/create", new CreateUserHandler())
            );
        }
    }
}
