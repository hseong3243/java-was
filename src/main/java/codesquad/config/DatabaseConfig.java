package codesquad.config;

import codesquad.database.UserDatabase;
import codesquad.database.UserSessionStorage;

public class DatabaseConfig {

    @Bean
    public UserDatabase userDatabase() {
        return new UserDatabase();
    }

    @Bean
    public UserSessionStorage userSessionStorage() {
        return new UserSessionStorage();
    }
}
