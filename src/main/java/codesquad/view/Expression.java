package codesquad.view;

import codesquad.web.ModelAndView;

public interface Expression {
    String handle(String template, ModelAndView mav);
}
