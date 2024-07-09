package codesquad.handler;

import codesquad.message.HttpMethod;
import codesquad.message.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public final class HandlerMapper {

    private static final Map<String, Handler> GET_HANDLERS = new HashMap<>();
    private static final Map<String, Handler> POST_HANDLERS = new HashMap<>();

    static {
        GET_HANDLERS.put("/user/list", new ListUserHandler());
        POST_HANDLERS.put("/user/create", new CreateUserHandler());
        POST_HANDLERS.put("/login", new LoginHandler());
    }

    public static Handler mapping(HttpRequest httpRequest) {
        if(httpRequest.method().equals(HttpMethod.GET)) {
            return Optional.ofNullable(GET_HANDLERS.get(httpRequest.requestUrl()))
                    .orElse(new StaticResourceHandler());
        } else {
            return Optional.ofNullable(POST_HANDLERS.get(httpRequest.requestUrl()))
                    .orElseThrow(
                            () -> new NoSuchElementException("리소스가 존재하지 않습니다. requestUrl=" + httpRequest.requestUrl()));
        }
    }
}
