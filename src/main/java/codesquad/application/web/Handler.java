package codesquad.application.web;

import codesquad.server.message.HttpRequest;

public interface Handler {
    ModelAndView handle(HttpRequest httpRequest);
}
