package codesquad.handler;

import codesquad.message.HttpStatusCode;
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
}
