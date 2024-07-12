package codesquad.application.config;

import codesquad.application.bean.Bean;
import codesquad.application.database.Database;
import codesquad.application.database.SessionStorage;
import codesquad.application.handler.MainHandler;
import codesquad.application.web.StaticResourceHandler;
import codesquad.application.handler.UserHandler;
import codesquad.application.handler.LoginHandler;

public class HandlerConfig {

    @Bean
    public UserHandler userHandler(Database database, SessionStorage sessionStorage) {
        return new UserHandler(database, sessionStorage);
    }

    @Bean
    public MainHandler mainHandler(Database database, SessionStorage sessionStorage) {
        return new MainHandler(database, sessionStorage);
    }

    @Bean
    public LoginHandler userLoginHandler(Database database, SessionStorage sessionStorage) {
        return new LoginHandler(database, sessionStorage);
    }

    @Bean
    public StaticResourceHandler staticResourceHandler() {
        return new StaticResourceHandler();
    }
}
