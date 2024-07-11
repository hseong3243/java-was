package codesquad.view;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.web.ModelAndView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TemplateEngineTest {

    @Nested
    @DisplayName("render 호출 시")
    class RenderTest {

        private String template = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>타이틀</title>
                </head>
                <body>
                    {{#if userId}}
                    <p>{{userId}}</p>
                    {{else}}
                    <a href="/login">Login</a>
                    {{/if}}
                </body>
                </html>""";

        @Test
        @DisplayName("if-else를 대체하고 값에 따른 뷰를 렌더링한다.")
        void replaceIfElse() {
            //given
            ModelAndView mav = new ModelAndView();
            mav.add("userId", "tester");

            //when
            String view = TemplateEngine.render(template, mav);

            //then
            assertThat(view)
                    .doesNotContain(
                            "{{#if userId}}",
                            "{{else}}",
                            "<a href=\"/login\">Login</a>",
                            "{{/if}}")
                    .doesNotContain("{{userId}}")
                    .contains("<p>tester</p>");
        }
    }
}