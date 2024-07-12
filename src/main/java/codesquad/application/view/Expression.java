package codesquad.application.view;

import codesquad.application.web.ModelAndView;

public interface Expression {
    String handle(String template, ModelAndView mav);
}
