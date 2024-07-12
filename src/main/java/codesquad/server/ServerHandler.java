package codesquad.server;

import codesquad.server.message.HttpRequest;
import codesquad.server.message.HttpResponse;

public interface ServerHandler {
    HttpResponse handle(HttpRequest request);
}
