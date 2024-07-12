package codesquad.application.config;

import codesquad.application.bean.Bean;
import codesquad.application.database.Database;
import codesquad.application.database.SessionStorage;

public class DatabaseConfig {

    @Bean
    public Database userDatabase() {
        return new Database();
    }

    @Bean
    public SessionStorage userSessionStorage() {
        return new SessionStorage();
    }
}
