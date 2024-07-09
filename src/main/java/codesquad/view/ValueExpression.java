package codesquad.view;

import codesquad.handler.ModelAndView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueExpression implements Expression{

    private static final String VALUE_PATTEN = "\\{\\{([\\s\\S]*?)}}";

    @Override
    public String handle(String template, ModelAndView mav) {
        Matcher matcher = Pattern.compile(VALUE_PATTEN).matcher(template);
        while (matcher.find()) {
            template = matcher.replaceAll((matchResult) -> {
                String key = matchResult.group().replace("{{", "").replace("}}", "");
                String value = mav.getModelValue(key);
                return value != null ? value : "";
            });
        }
        return template;
    }
}
