package codesquad.view;

import codesquad.handler.ModelAndView;

public interface Expression {
    String handle(String template, ModelAndView mav);
}
