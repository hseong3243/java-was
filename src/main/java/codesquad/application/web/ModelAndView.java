package codesquad.application.web;

import codesquad.server.message.HttpStatusCode;
import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> model = new HashMap<>();
    private final byte[] view;
    private final HttpStatusCode statusCode;

    public ModelAndView() {
        this(new byte[]{});
    }

    public ModelAndView(byte[] view) {
        this(view, HttpStatusCode.OK);
    }

    public ModelAndView(HttpStatusCode statusCode) {
        this(new byte[]{}, statusCode);
    }

    public ModelAndView(byte[] view, HttpStatusCode statusCode) {
        this.view = view;
        this.statusCode = statusCode;
    }

    public void add(String key, String value) {
        model.put(key, value);
    }

    public String getModelValue(String key) {
        return model.get(key);
    }

    public byte[] getView() {
        return view;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setCookie(String sessionId, String path, boolean httpOnly) {
        StringBuilder sb = new StringBuilder("SID=").append(sessionId).append("; ")
                .append("Path=").append(path);
        if(httpOnly) {
            sb.append("; ").append("HttpOnly");
        }
        headers.put("Set-Cookie", sb.toString());
    }

    public void invalidateCookie() {
        String sb = "SID=-1; Max-Age=0";
        headers.put("Set-Cookie", sb);
    }
}
