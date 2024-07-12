package codesquad.application.bean;

import codesquad.application.config.DatabaseConfig;
import codesquad.application.config.HandlerConfig;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {
    private final Map<String, Object> context = new ConcurrentHashMap<>();

    public void start() {
        try {
            register(DatabaseConfig.class);
            register(HandlerConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("리플렉션 에러 발생", e);
        }
    }

    private void register(Class<?> clazz)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        // 기본 생성자 획득
        Constructor<?> constructor = clazz.getConstructor();
        Object configObj = constructor.newInstance();

        // 퍼블릭 메서드 순회
        for (Method method : clazz.getMethods()) {
            if(!method.isAnnotationPresent(Bean.class)) {
                continue;
            }
            if(context.containsKey(method.getName())) {
                continue;
            }
            String beanName = method.getName();

            // 메서드 파라미터로 사용할 빈 조회
            Object[] parameters = new Object[method.getParameterCount()];
            Class<?>[] parameterTypes = method.getParameterTypes();
            for(int i=0; i<parameters.length; i++) {
                Object bean = getBean(parameterTypes[i]);
                parameters[i] = bean;
            }
            Object bean = method.invoke(configObj, parameters);
            context.put(beanName, bean);
        }
    }

    public Object getBean(String beanName) {
        return Optional.ofNullable(context.get(beanName))
                .orElseThrow(() -> new RuntimeException("존재하지 않는 빈입니다."));
    }

    public <T> T getBean(Class<T> clazz) {
        return context.values().stream()
                .filter(obj -> obj.getClass().equals(clazz))
                .findFirst()
                .map(clazz::cast)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 빈입니다."));
    }

    public List<Object> getBeans() {
        return context.values().stream().toList();
    }
}
