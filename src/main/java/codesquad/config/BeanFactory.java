package codesquad.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {
    private final Map<String, Object> context = new ConcurrentHashMap<>();

    public void start() {
        try {
            register(HandlerConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("리플렉션 에러 발생", e);
        }
    }

    private void register(Class<?> clazz)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Constructor<?> constructor = clazz.getConstructor();
        Object configObj = constructor.newInstance();
        for (Method method : clazz.getMethods()) {
            if(!method.isAnnotationPresent(Bean.class)) {
                continue;
            }
            if(context.containsKey(method.getName())) {
                continue;
            }
            String beanName = method.getName();
            Object bean = method.invoke(configObj);
            context.put(beanName, bean);
        }
    }

    public Object getBean(String beanName) {
        return Optional.ofNullable(context.get(beanName))
                .orElseThrow(() -> new RuntimeException("존재하지 않는 빈입니다."));
    }

    public Object getBean(Class<?> clazz) {
        return context.values().stream()
                .filter(obj -> obj.getClass().equals(clazz))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("존재하지 않는 빈입니다."));
    }
}
