package codesquad.handler;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private final Map<String, String> model = new HashMap<>();
    private final String view;

    public ModelAndView() {
        this.view = "";
    }

    public ModelAndView(String view) {
        this.view = view;
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
}
