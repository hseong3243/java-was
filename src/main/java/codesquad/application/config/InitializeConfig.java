package codesquad.application.config;

import codesquad.application.bean.Bean;
import codesquad.application.bean.BeanFactory;
import codesquad.application.init.CSVDatabaseInit;
import codesquad.application.init.DatabaseInit;
import codesquad.application.web.AnnotationHandlerMapping;

public class InitializeConfig {

    @Bean
    public DatabaseInit databaseInit() {
        return new CSVDatabaseInit("realcsv");
    }

    @Bean
    public AnnotationHandlerMapping annotationHandlerMapping(BeanFactory beanFactory) {
        return new AnnotationHandlerMapping(beanFactory);
    }
}
