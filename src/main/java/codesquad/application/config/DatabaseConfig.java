package codesquad.application.config;

import codesquad.application.bean.Bean;
import codesquad.application.database.ArticleDatabase;
import codesquad.application.database.ArticleJdbcDatabase;
import codesquad.application.file.ImageStore;
import codesquad.application.database.SessionJdbcStorage;
import codesquad.application.database.SessionStorage;
import codesquad.application.database.UserDatabase;
import codesquad.application.database.UserJdbcDatabase;

public class DatabaseConfig {

    @Bean
    public UserDatabase userDatabase() {
        return new UserJdbcDatabase();
    }

    @Bean
    public SessionStorage sessionStorage() {
        return new SessionJdbcStorage();
    }

    @Bean
    public ArticleDatabase articleDatabase() {
        return new ArticleJdbcDatabase();
    }

    @Bean
    public ImageStore imageDatabase() {
        return new ImageStore();
    }
}
