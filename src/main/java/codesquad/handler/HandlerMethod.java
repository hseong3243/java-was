package codesquad.handler;

import codesquad.message.HttpRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandlerMethod {
    private final Object bean;
    private final Method method;

    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public ModelAndView invoke(HttpRequest httpRequest) throws InvocationTargetException, IllegalAccessException {
        return (ModelAndView) method.invoke(bean, httpRequest);
    }
}
