package codesquad.view;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.web.ModelAndView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ValueExpressionTest {

    @Nested
    @DisplayName("handle 호출 시")
    class HandleTest {

        private ValueExpression valueExpression;
        private String template;

        @BeforeEach
        void setUp() {
            valueExpression = new ValueExpression();
            template = """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <title>타이틀</title>
                    </head>
                    <body>
                        {{userId}}
                    </body>
                    </html>""";
        }

        @Test
        @DisplayName("{{key}} 블럭을 모델에 담긴 값으로 대체한다.")
        void test() {
            //given
            ModelAndView modelAndView = new ModelAndView(new byte[]{});
            modelAndView.add("userId", "안녕하세요.");

            //when
            String view = valueExpression.handle(template, modelAndView);

            //then
            assertThat(view).doesNotContain("{{userId}}")
                    .contains("안녕하세요.");
        }
    }
}