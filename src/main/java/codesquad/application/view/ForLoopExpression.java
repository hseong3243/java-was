package codesquad.application.view;

import codesquad.application.web.ModelAndView;
import codesquad.server.message.HttpStatusCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForLoopExpression implements Expression{

    private static final Pattern FOR_PATTERN = Pattern.compile("\\{\\{for\\s+(\\w+)\\s+in\\s+(\\w+)\\}\\}(.+?)\\{\\{/for\\}\\}", Pattern.DOTALL);

    @Override
    public String handle(String template, ModelAndView mav) {
        StringBuilder stringBuilder = new StringBuilder();
        Matcher matcher = FOR_PATTERN.matcher(template);
        while (matcher.find()) {
            String itemName = matcher.group(1);
            String listName = matcher.group(2);
            String loopContent = matcher.group(3);

            List<?> items = Optional.ofNullable(mav.get(listName))
                    .map(obj -> (List<?>) obj)
                    .orElse(new ArrayList<>());
            StringBuilder sb = new StringBuilder();
            for (Object item : items) {
                ModelAndView modelAndView = new ModelAndView(HttpStatusCode.OK);
                modelAndView.add(itemName, item);
                sb.append(processNestedExpressions(loopContent, modelAndView));
            }
            matcher.appendReplacement(stringBuilder, Matcher.quoteReplacement(sb.toString()));
        }
        matcher.appendTail(stringBuilder);
        return stringBuilder.toString();
    }

    private String processNestedExpressions(String content, ModelAndView mav) {
        ConditionalExpression conditionalExpression = new ConditionalExpression();
        ValueExpression valueExpression = new ValueExpression();
        content = conditionalExpression.handle(content, mav);
        content = valueExpression.handle(content, mav);
        return content;
    }
}
