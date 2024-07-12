package codesquad;

import codesquad.message.HttpRequest;
import codesquad.message.HttpResponse;

public interface ServerHandler {
    HttpResponse handle(HttpRequest request);
}
