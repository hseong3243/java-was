package codesquad.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class HandlerMapper {

    private static final Map<String, Handler> HANDLERS = new HashMap<>();

    static {
        HANDLERS.put("/user/create", new CreateUserHandler());
    }

    public static Handler mapping(String url) {
        return Optional.ofNullable(HANDLERS.get(url))
                .orElse(new StaticResourceHandler());
    }
}
