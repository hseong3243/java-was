package codesquad.application.web;

import codesquad.server.message.HttpRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandlerMethod {
    private final Object bean;
    private final Method method;

    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public ModelAndView invoke(HttpRequest httpRequest) throws Throwable {
        try {
            return (ModelAndView) method.invoke(bean, httpRequest);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }
}
