package codesquad.base;

import codesquad.bean.BeanFactory;
import codesquad.web.AnnotationHandlerMapping;
import codesquad.web.RequestDispatcher;
import org.junit.jupiter.api.BeforeEach;

public abstract class ApplicationTest {

    protected BeanFactory beanFactory;
    protected AnnotationHandlerMapping handlerMapping;
    protected RequestDispatcher requestDispatcher;

    @BeforeEach
    void setUp() {
        beanFactory = new BeanFactory();
        beanFactory.start();

        handlerMapping = new AnnotationHandlerMapping();
        handlerMapping.init(beanFactory);

        requestDispatcher = new RequestDispatcher(handlerMapping);
    }
}
