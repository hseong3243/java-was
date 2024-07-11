package codesquad.config;

import codesquad.database.Database;
import codesquad.database.SessionStorage;

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
