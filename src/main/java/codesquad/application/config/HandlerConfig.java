package codesquad.application.config;

import codesquad.application.bean.Bean;
import codesquad.application.database.ArticleDatabase;
import codesquad.application.file.ImageStore;
import codesquad.application.database.SessionStorage;
import codesquad.application.database.UserDatabase;
import codesquad.application.handler.ArticleHandler;
import codesquad.application.handler.LoginHandler;
import codesquad.application.handler.MainHandler;
import codesquad.application.handler.UserHandler;
import codesquad.application.web.StaticResourceHandler;

public class HandlerConfig {

    @Bean
    public UserHandler userHandler(UserDatabase userDatabase, SessionStorage sessionStorage) {
        return new UserHandler(userDatabase, sessionStorage);
    }

    @Bean
    public MainHandler mainHandler(UserDatabase userDatabase, SessionStorage sessionStorage) {
        return new MainHandler(userDatabase, sessionStorage);
    }

    @Bean
    public LoginHandler userLoginHandler(UserDatabase userDatabase, SessionStorage sessionStorage) {
        return new LoginHandler(userDatabase, sessionStorage);
    }

    @Bean
    public StaticResourceHandler staticResourceHandler() {
        return new StaticResourceHandler();
    }

    @Bean
    public ArticleHandler articleHandler(SessionStorage sessionStorage, ArticleDatabase articleDatabase,
                                         UserDatabase userDatabase, ImageStore imageStore) {
        return new ArticleHandler(articleDatabase, sessionStorage, userDatabase, imageStore);
    }
}
