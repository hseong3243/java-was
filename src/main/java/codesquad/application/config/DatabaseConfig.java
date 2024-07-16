package codesquad.application.config;

import codesquad.application.bean.Bean;
import codesquad.application.database.ArticleDatabase;
import codesquad.application.database.UserDatabase;
import codesquad.application.database.UserMemoryDatabase;
import codesquad.application.database.SessionStorage;

public class DatabaseConfig {

    @Bean
    public UserDatabase userDatabase() {
        return new UserMemoryDatabase();
    }

    @Bean
    public SessionStorage userSessionStorage() {
        return new SessionStorage();
    }

    @Bean
    public ArticleDatabase articleDatabase() {
        return new ArticleDatabase();
    }
}
