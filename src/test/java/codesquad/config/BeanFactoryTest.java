package codesquad.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import codesquad.database.UserDatabase;
import codesquad.database.UserSessionStorage;
import codesquad.handler.UserHandler;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BeanFactoryTest {

    @Nested
    @DisplayName("start 호출 시")
    class StartTest {

        private BeanFactory beanFactory;

        @BeforeEach
        void setUp() {
            beanFactory = new BeanFactory();
        }

        @ParameterizedTest
        @MethodSource("databaseConfig")
        @DisplayName("DatabaseConfig가 등록된다.")
        void registerDatabaseConfig(Class<?> clazz, String className) {
            //given

            //when
            beanFactory.start();

            //then
            Object bean = beanFactory.getBean(clazz);
            assertThat(bean.getClass().getSimpleName()).isEqualTo(className);
        }

        private static Stream<Arguments> databaseConfig() {
            return Stream.of(
                    Arguments.arguments(UserDatabase.class, "UserDatabase"),
                    Arguments.arguments(UserSessionStorage.class, "UserSessionStorage")
            );
        }

        @Test
        @DisplayName("HandlerConfig가 등록된다.")
        void registerHandlerConfig() {
            //given

            //when
            beanFactory.start();

            //then
            Object userHandler = beanFactory.getBean("userHandler");
            assertThat(userHandler).isInstanceOf(UserHandler.class);
        }
    }

    @Nested
    @DisplayName("getBean 호출 시")
    class GetBeanTest {

        private BeanFactory beanFactory;

        @BeforeEach
        void setUp() {
            beanFactory = new BeanFactory();
        }

        @Test
        @DisplayName("빈 이름에 매칭되는 빈이 반환된다.")
        void matchBeanName() {
            //given
            beanFactory.start();

            //when
            Object userHandler = beanFactory.getBean("userHandler");

            //then
            assertThat(userHandler).isInstanceOf(UserHandler.class);
        }

        @Test
        @DisplayName("빈 타입에 매칭되는 빈이 반환된다.")
        void matchBeanType() {
            //given
            beanFactory.start();

            //when
            Object userHandler = beanFactory.getBean(UserHandler.class);

            //then
            assertThat(userHandler).isInstanceOf(UserHandler.class);
        }

        @Test
        @DisplayName("예외: 매칭되는 빈이 없으면")
        void exception_WhenNoMatchBean() {
            //given
            beanFactory.start();

            //when
            Exception exception = catchException(() -> beanFactory.getBean("handler"));

            //then
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }
}