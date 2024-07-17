package codesquad.application.view;

import codesquad.application.web.ModelAndView;
import java.lang.reflect.Method;
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
                if(key.contains(".")) {
                    String[] split = key.split("\\.");
                    String objectName = split[0];
                    Object object = mav.get(objectName);
                    Object value;
                    try {
                        Method method = object.getClass().getMethod(split[1]);
                        value = method.invoke(object);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("유효하지 않는 값입니다.",e);
                    }
                    return value == null ? "" : String.valueOf(value);
                }
                String value = mav.getModelValue(key);
                return value != null ? value : "";
            });
        }
        return template;
    }
}
