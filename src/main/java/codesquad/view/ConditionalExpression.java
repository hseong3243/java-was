package codesquad.view;

import codesquad.web.ModelAndView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionalExpression implements Expression {
    private static final String WORD_PATTERN = "([\\s\\S]*?)";
    private static final String IF_PATTERN = "\\{\\{#if (\\w+)}}";
    private static final String ELSE_PATTERN = "\\{\\{else}}";
    private static final String END_OF_IF_PATTERN = "\\{\\{/if}}";

    private static final String IF_ELSE_END_BLOCK_PATTERN =
            IF_PATTERN + WORD_PATTERN + ELSE_PATTERN + WORD_PATTERN + END_OF_IF_PATTERN;
    private static final String IF_ELSE_BLOCK_PATTERN = IF_PATTERN + WORD_PATTERN + ELSE_PATTERN;
    private static final String ELSE_END_BLOCK_PATTERN = ELSE_PATTERN + WORD_PATTERN + END_OF_IF_PATTERN;

    @Override
    public String handle(String template, ModelAndView mav) {
        Matcher matcher = Pattern.compile(IF_ELSE_END_BLOCK_PATTERN).matcher(template);
        while (matcher.find()) {
            String condition = getCondition(template);
            if (mav.getModelValue(condition) != null) {
                template = template.replaceFirst(IF_PATTERN, "");
                template = template.replaceFirst(ELSE_END_BLOCK_PATTERN, "");
            } else {
                template = template.replaceFirst(IF_ELSE_BLOCK_PATTERN, "");
                template = template.replaceFirst(END_OF_IF_PATTERN, "");
            }
        }
        return template;
    }

    private String getCondition(String template) {
        int beginOfIf = template.indexOf("{{#if");
        int endOfIf = template.indexOf("}}", beginOfIf);
        String condition = template.substring(beginOfIf, endOfIf).replace("{{#if", "");
        return condition.trim();
    }
}
