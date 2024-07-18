package codesquad.base;

import codesquad.application.bean.BeanFactory;
import codesquad.application.database.memory.SessionMemoryStorage;
import codesquad.application.database.SessionStorage;
import codesquad.application.web.AnnotationHandlerMapping;
import codesquad.application.web.RequestDispatcher;
import org.junit.jupiter.api.BeforeEach;

public abstract class ApplicationTest {

    protected BeanFactory beanFactory;
    protected AnnotationHandlerMapping handlerMapping;
    protected RequestDispatcher requestDispatcher;
    protected SessionStorage sessionStorage;

    @BeforeEach
    void setUp() {
        beanFactory = new BeanFactory();
        beanFactory.start();

        handlerMapping = new AnnotationHandlerMapping();
        handlerMapping.init(beanFactory);

        sessionStorage = new SessionMemoryStorage();

        requestDispatcher = new RequestDispatcher(handlerMapping, sessionStorage);
    }
}
