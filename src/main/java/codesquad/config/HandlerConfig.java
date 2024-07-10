package codesquad.config;

import codesquad.database.UserDatabase;
import codesquad.database.UserSessionStorage;
import codesquad.handler.UserHandler;

public class HandlerConfig {

    @Bean
    public UserHandler userHandler(UserDatabase userDatabase, UserSessionStorage userSessionStorage) {
        return new UserHandler(userDatabase, userSessionStorage);
    }
}
