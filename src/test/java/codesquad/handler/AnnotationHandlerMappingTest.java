package codesquad.handler;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.bean.BeanFactory;
import codesquad.fixture.HttpFixture;
import codesquad.message.HttpMethod;
import codesquad.message.HttpRequest;
import codesquad.web.AnnotationHandlerMapping;
import codesquad.web.HandlerMethod;
import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AnnotationHandlerMappingTest {

    private BeanFactory beanFactory;
    private AnnotationHandlerMapping handlerMapping;

    @BeforeEach
    void setUp() {
        beanFactory = new BeanFactory();
        beanFactory.start();
        handlerMapping = new AnnotationHandlerMapping();
    }

    @Nested
    @DisplayName("init 호출 시")
    class InitTest {

        @Test
        @DisplayName("정적 리소스 핸들러가 등록된다.")
        void test() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET)
                    .path("/asdf")
                    .build();
            HttpRequest httpRequest = HttpRequest.parse(new BufferedReader(new StringReader(rawHttpMessage)));

            //when
            handlerMapping.init(beanFactory);

            //then
            HandlerMethod handlerMethod = handlerMapping.getHandler(httpRequest);
            assertThat(handlerMethod).isNotNull();
        }
    }
}