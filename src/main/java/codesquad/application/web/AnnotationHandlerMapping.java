package codesquad.application.web;

import codesquad.application.bean.BeanFactory;
import codesquad.application.init.Initializer;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationHandlerMapping implements Initializer {

    private final Map<String, Map<HttpMethod, HandlerMethod>> handlers = new ConcurrentHashMap<>();
    private HandlerMethod staticResourceHandler;
    private final BeanFactory beanFactory;

    public AnnotationHandlerMapping(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void init() {
        for (Object bean : beanFactory.getBeans()) {
            for (Method method : bean.getClass().getMethods()) {
                registerDynamicHandler(bean, method);
                registerStaticResourceHandler(bean, method);
            }
        }
    }

    private void registerDynamicHandler(Object bean, Method method) {
        if(method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
            String path = annotation.path();
            HttpMethod httpMethod = annotation.method();
            Map<HttpMethod, HandlerMethod> httpMethodHandlerMethodMap = handlers.computeIfAbsent(path,
                    key -> new ConcurrentHashMap<>());
            httpMethodHandlerMethodMap.put(httpMethod, new HandlerMethod(bean, method));
        }
    }

    private void registerStaticResourceHandler(Object bean, Method method) {
        if(bean instanceof StaticResourceHandler) {
            if(method.getReturnType().equals(ModelAndView.class)) {
                this.staticResourceHandler = new HandlerMethod(bean, method);
            }
        }
    }

    public HandlerMethod getHandler(HttpRequest httpRequest) {
        Optional<Map<HttpMethod, HandlerMethod>> optionalHandlerMethods = Optional.ofNullable(
                handlers.get(httpRequest.requestUrl()));
        if(optionalHandlerMethods.isEmpty()) {
            return staticResourceHandler;
        }
        Map<HttpMethod, HandlerMethod> requestHandlerMethods = optionalHandlerMethods.get();
        return Optional.ofNullable(requestHandlerMethods.get(httpRequest.method()))
                .orElseThrow(() -> new MethodNotAllowedException(requestHandlerMethods.keySet()));
    }
}
