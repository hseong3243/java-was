package codesquad.application.config;

import codesquad.application.init.CSVDatabaseInit;
import codesquad.application.init.DatabaseInit;
import codesquad.application.bean.Bean;

public class InitializeConfig {

    @Bean
    public DatabaseInit databaseInit() {
        return new CSVDatabaseInit("realcsv");
    }
}
