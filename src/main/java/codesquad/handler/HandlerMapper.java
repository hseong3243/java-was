package codesquad.handler;

import codesquad.message.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public final class HandlerMapper {

    private static final Map<String, Handler> HANDLERS = new HashMap<>();
    private static final Map<String, Handler> POST_HANDLERS = new HashMap<>();

    static {
        POST_HANDLERS.put("/user/create", new CreateUserHandler());
    }

    public static Handler mapping(HttpRequest httpRequest) {
        if(httpRequest.method().equals("GET")) {
            return Optional.ofNullable(HANDLERS.get(httpRequest.requestUrl()))
                    .orElse(new StaticResourceHandler());
        } else {
            return Optional.ofNullable(POST_HANDLERS.get(httpRequest.requestUrl()))
                    .orElseThrow(
                            () -> new NoSuchElementException("리소스가 존재하지 않습니다. requestUrl=" + httpRequest.requestUrl()));
        }
    }
}
