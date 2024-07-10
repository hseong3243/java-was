package codesquad.config;

import codesquad.handler.UserHandler;

public class HandlerConfig {

    @Bean
    public UserHandler userHandler() {
        return new UserHandler();
    }
}
