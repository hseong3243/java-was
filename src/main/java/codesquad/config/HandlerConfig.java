package codesquad.config;

import codesquad.database.UserDatabase;
import codesquad.database.UserSessionStorage;
import codesquad.handler.MainHandler;
import codesquad.handler.StaticResourceHandler;
import codesquad.handler.UserHandler;
import codesquad.handler.LoginHandler;

public class HandlerConfig {

    @Bean
    public UserHandler userHandler(UserDatabase userDatabase, UserSessionStorage userSessionStorage) {
        return new UserHandler(userDatabase, userSessionStorage);
    }

    @Bean
    public MainHandler mainHandler(UserDatabase userDatabase, UserSessionStorage userSessionStorage) {
        return new MainHandler(userDatabase, userSessionStorage);
    }

    @Bean
    public LoginHandler userLoginHandler(UserDatabase userDatabase, UserSessionStorage userSessionStorage) {
        return new LoginHandler(userDatabase, userSessionStorage);
    }

    @Bean
    public StaticResourceHandler staticResourceHandler() {
        return new StaticResourceHandler();
    }
}
