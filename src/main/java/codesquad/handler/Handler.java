package codesquad.handler;

import codesquad.message.HttpRequest;

public interface Handler {
    ModelAndView handle(HttpRequest httpRequest);
}
