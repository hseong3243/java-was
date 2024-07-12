package codesquad.application.view;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.application.view.ConditionalExpression;
import codesquad.application.web.ModelAndView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ConditionalExpressionTest {

    @Nested
    @DisplayName("render 호출 시")
    class RenderTest {

        private ConditionalExpression conditionalExpression;
        private String template = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>타이틀</title>
                </head>
                <body>
                    {{#if userId}}
                    <a href="/logout">Logout</a>
                    {{else}}
                    <a href="/login">Login</a>
                    {{/if}}
                </body>
                </html>""";

        @BeforeEach
        void setUp() {
            conditionalExpression = new ConditionalExpression();
            template = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>타이틀</title>
                </head>
                <body>
                    {{#if userId}}
                    <a href="/logout">Logout</a>
                    {{else}}
                    <a href="/login">Login</a>
                    {{/if}}
                </body>
                </html>""";
        }

        @Test
        @DisplayName("조건문이 참이면 {{else}}~{{/if}} 블럭을 제거한다.")
        void true_ThenReplaceElseEndOfIf() {
            //given
            ModelAndView mav = new ModelAndView();
            mav.add("userId", "tester");

            //when
            String view = conditionalExpression.handle(template, mav);

            //then
            assertThat(view).doesNotContain("{{#if userId}}", "{{else}}", "{{/if}}")
                    .contains("<a href=\"/logout\">Logout</a>");
        }

        @Test
        @DisplayName("조건문이 거짓이면 {{#if ~}}~{{else}} 블럭을 제거한다.")
        void false_ThenReplaceIfElse() {
            //given
            ModelAndView mav = new ModelAndView();

            //when
            String view = conditionalExpression.handle(template, mav);

            //then
            assertThat(view).doesNotContain("{{#if userId}}", "{{else}}", "{{/if}}")
                    .contains("<a href=\"/login\">Login</a>");
        }
    }
}