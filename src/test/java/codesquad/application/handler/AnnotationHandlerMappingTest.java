package codesquad.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import codesquad.application.web.HandlerMethod;
import codesquad.application.web.MethodNotAllowedException;
import codesquad.application.web.StaticResourceHandler;
import codesquad.base.ApplicationTest;
import codesquad.fixture.HttpFixture;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpRequest;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AnnotationHandlerMappingTest extends ApplicationTest {

    @Nested
    @DisplayName("init 호출 시")
    class InitTest {

        @Test
        @DisplayName("정적 리소스 핸들러가 등록된다.")
        void registerStaticHandler() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET)
                    .path("/asdf")
                    .buildToRawHttpMessage();
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(rawHttpMessage.getBytes()));
            HttpRequest httpRequest = HttpRequest.parse(bis);

            //when
            handlerMapping.init(beanFactory);

            //then
            HandlerMethod handlerMethod = handlerMapping.getHandler(httpRequest);
            assertThat(handlerMethod).isNotNull();
        }

        @Test
        @DisplayName("게시글 핸들러가 등록된다.")
        void registerArticleHandler() {
            //given
            String rawHttpMessage = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/article")
                    .buildToRawHttpMessage();
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(rawHttpMessage.getBytes()));
            HttpRequest httpRequest = HttpRequest.parse(bis);

            //when
            handlerMapping.init(beanFactory);

            //then
            HandlerMethod handlerMethod = handlerMapping.getHandler(httpRequest);
            assertThat(handlerMethod).isNotNull();
        }
    }

    @Nested
    @DisplayName("getHandler 호출 시")
    class GetHandlerTest {

        @Test
        @DisplayName("요청 url에 매핑된 핸들러 목록이 없다면 정적 리소스 핸들러를 반환한다.")
        void noMatchHandlers_ThenReturnStaticResourceHandler() {
            //given
            HttpRequest httpRequest = HttpFixture.builder()
                    .method(HttpMethod.GET).path("/hello")
                    .buildToHttpRequest();

            //when
            HandlerMethod handlerMethod = handlerMapping.getHandler(httpRequest);

            //then
            assertThat(handlerMethod.getBean()).isInstanceOf(StaticResourceHandler.class);
        }

        @Test
        @DisplayName("예외(methodNotAllowed): 요청 url에 매핑된 핸들러가 있으나 지원하지 않는 메서드이면")
        void methodNotAllowed_WhenNotAllowedMethod() {
            //given
            HttpRequest httpRequest = HttpFixture.builder()
                    .method(HttpMethod.POST).path("/")
                    .buildToHttpRequest();

            //when
            Exception exception = catchException(() -> handlerMapping.getHandler(httpRequest));

            //then
            assertThat(exception).isInstanceOf(MethodNotAllowedException.class);
        }
    }
}