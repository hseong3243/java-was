package codesquad.application.web;

import codesquad.application.util.ResourceUtils;
import codesquad.server.message.HttpMethod;
import codesquad.server.message.HttpResponse;
import codesquad.server.message.HttpStatusCode;
import java.util.Set;

public final class HttpResponseFactory {

    private static final String HTTP1_1 = "HTTP/1.1";

    public static HttpResponse badRequest() {
        HttpResponse httpResponse = new HttpResponse(HTTP1_1, HttpStatusCode.BAD_REQUEST, ResourceUtils.getStaticFile("/error/badRequest.html"));
        httpResponse.addHeader("Content-Type", "text/html; charset=UTF-8");
        return httpResponse;
    }

    public static HttpResponse notFound() {
        HttpResponse httpResponse = new HttpResponse(HTTP1_1, HttpStatusCode.NOT_FOUND,
                ResourceUtils.getStaticFile("/error/notfound.html"));
        httpResponse.addHeader("Content-Type", "text/html; charset=UTF-8");
        return httpResponse;
    }

    public static HttpResponse methodNotAllowed(Set<HttpMethod> allowedMethods) {
        HttpResponse httpResponse = new HttpResponse(HTTP1_1, HttpStatusCode.METHOD_NOT_ALLOWED,
                ResourceUtils.getStaticFile("/error/methodNotAllowed.html"));
        httpResponse.addHeader("Content-Type", "text/html; charset=utf-8");
        StringBuilder sb = new StringBuilder();
        allowedMethods.forEach(method -> sb.append(method.name()).append("; "));
        httpResponse.addHeader("Allow", sb.toString());
        return httpResponse;
    }

    public static HttpResponse internalServerError() {
        HttpResponse httpResponse = new HttpResponse(HTTP1_1, HttpStatusCode.INTERNAL_SERVER_ERROR,
                ResourceUtils.getStaticFile("/error/internalServerError.html"));
        httpResponse.addHeader("Content-Type", "text/html; charset=UTF-8");
        return httpResponse;
    }

    public static HttpResponse invalidateSession() {
        HttpResponse httpResponse = new HttpResponse(HTTP1_1, HttpStatusCode.FOUND, "");
        httpResponse.addHeader("Location", "/login");
        httpResponse.addHeader("Set-Cookie", "SID=-1; Max-Age=0");
        return httpResponse;
    }

    private HttpResponseFactory() {
    }
}
