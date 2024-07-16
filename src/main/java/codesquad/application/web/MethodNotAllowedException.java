package codesquad.application.web;

import codesquad.server.message.HttpMethod;
import java.util.Set;

public class MethodNotAllowedException extends RuntimeException {

    private final Set<HttpMethod> allowedMethods;

    public MethodNotAllowedException(Set<HttpMethod> httpMethods) {
        this.allowedMethods = httpMethods;
    }
}
