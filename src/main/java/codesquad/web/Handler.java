package codesquad.web;

import codesquad.message.HttpRequest;

public interface Handler {
    ModelAndView handle(HttpRequest httpRequest);
}
