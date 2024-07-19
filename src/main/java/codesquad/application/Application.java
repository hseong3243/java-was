package codesquad.application;

import codesquad.application.bean.BeanFactory;
import codesquad.application.database.SessionStorage;
import codesquad.server.Server;
import codesquad.application.web.AnnotationHandlerMapping;
import codesquad.application.web.RequestDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static BeanFactory run() {
        return new Application().start();
    }

    public Application() {
    }

    public BeanFactory start() {
        log.debug("h2 인메모리 데이터베이스를 실행합니다.");
        CSVDatabaseInit databaseInit = new CSVDatabaseInit("realcsv");
        databaseInit.init();

        log.debug("컨텍스트를 실행합니다.");
        BeanFactory beanFactory = new BeanFactory();
        beanFactory.start();

        log.debug("핸들러 매퍼를 초기화합니다.");
        AnnotationHandlerMapping annotationHandlerMapping = new AnnotationHandlerMapping();
        annotationHandlerMapping.init(beanFactory);

        log.debug("요청 디스패처를 초기화합니다.");
        SessionStorage sessionStorage = beanFactory.getBean(SessionStorage.class);
        RequestDispatcher requestDispatcher = new RequestDispatcher(annotationHandlerMapping, sessionStorage);

        log.debug("서버 시작");
        Server server = new Server();
        server.start(requestDispatcher);

        return beanFactory;
    }
}
