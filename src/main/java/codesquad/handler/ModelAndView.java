package codesquad.handler;

import codesquad.message.HttpStatusCode;
import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> model = new HashMap<>();
    private final String view;
    private final HttpStatusCode statusCode;

    public ModelAndView() {
        this("");
    }

    public ModelAndView(String view) {
        this(view, HttpStatusCode.OK);
    }

    public ModelAndView(String view, HttpStatusCode statusCode) {
        this.view = view;
        this.statusCode = statusCode;
    }

    public void add(String key, String value) {
        model.put(key, value);
    }

    public String getModelValue(String key) {
        return model.get(key);
    }

    public String getView() {
        return view;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
