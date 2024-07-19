package codesquad.application.init;

import codesquad.application.bean.BeanFactory;
import java.util.ArrayList;
import java.util.List;

public class ApplicationInitializer implements Initializer{

    private final List<Initializer> initializers = new ArrayList<>();

    public ApplicationInitializer(BeanFactory beanFactory) {
        this.initializers.addAll(
                beanFactory.getBeans(Initializer.class)
                        .stream()
                        .map(obj -> (Initializer) obj)
                        .toList()
        );
    }

    @Override
    public void init() {
        for (Initializer initializer : initializers) {
            initializer.init();
        }
    }

    public void addInitializer(Initializer initializer) {
        initializers.add(initializer);
    }
}
