package codesquad.config;

import codesquad.bean.Bean;
import codesquad.database.Database;
import codesquad.database.SessionStorage;
import codesquad.handler.MainHandler;
import codesquad.web.StaticResourceHandler;
import codesquad.handler.UserHandler;
import codesquad.handler.LoginHandler;

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
