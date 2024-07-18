package codesquad.application.config;

import codesquad.application.bean.Bean;
import codesquad.application.database.ArticleDatabase;
import codesquad.application.database.SessionStorage;
import codesquad.application.database.UserDatabase;
import codesquad.application.database.csv.ArticleCsvDatabase;
import codesquad.application.database.csv.UserCsvDatabase;
import codesquad.application.database.memory.SessionMemoryStorage;
import codesquad.application.file.ImageStore;

public class DatabaseConfig {

    @Bean
    public UserDatabase userDatabase() {
        return new UserCsvDatabase();
    }

    @Bean
    public SessionStorage sessionStorage() {
        return new SessionMemoryStorage();
    }

    @Bean
    public ArticleDatabase articleDatabase() {
        return new ArticleCsvDatabase();
    }

    @Bean
    public ImageStore imageDatabase() {
        return new ImageStore();
    }
}
