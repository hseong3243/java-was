package codesquad.view;

import codesquad.web.ModelAndView;
import java.util.ArrayList;
import java.util.List;

public class TemplateEngine {

    private static final List<Expression> EXPRESSIONS = new ArrayList<>();

    static {
        EXPRESSIONS.add(new ConditionalExpression());
        EXPRESSIONS.add(new ValueExpression());
    }

    public static String render(String template, ModelAndView mav) {
        for (Expression expression : EXPRESSIONS) {
            template = expression.handle(template, mav);
        }
        return template;
    }
}
