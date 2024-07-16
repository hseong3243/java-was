package codesquad.application.config;

import codesquad.application.bean.Bean;
import codesquad.application.database.ArticleDatabase;
import codesquad.application.database.ArticleMemoryDatabase;
import codesquad.application.database.SessionStorage;
import codesquad.application.database.UserDatabase;
import codesquad.application.database.UserMemoryDatabase;
import codesquad.application.database.SessionMemoryStorage;

public class DatabaseConfig {

    @Bean
    public UserDatabase userDatabase() {
        return new UserMemoryDatabase();
    }

    @Bean
    public SessionStorage sessionStorage() {
        return new SessionMemoryStorage();
    }

    @Bean
    public ArticleDatabase articleDatabase() {
        return new ArticleMemoryDatabase();
    }
}
