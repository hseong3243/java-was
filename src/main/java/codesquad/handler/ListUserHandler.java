package codesquad.handler;

import codesquad.database.DataBase;
import codesquad.message.HttpRequest;
import codesquad.message.HttpStatusCode;
import codesquad.model.User;
import codesquad.util.ResourceUtils;

public class ListUserHandler implements Handler {

    @Override
    public ModelAndView handle(HttpRequest httpRequest) {
        String sessionId = httpRequest.cookies().get("SID");
        if (sessionId == null) {
            ModelAndView mav = new ModelAndView(HttpStatusCode.FOUND);
            mav.addHeader("Location", "/login");
            return mav;
        }
        StringBuilder sb = new StringBuilder();
        for (User user : DataBase.findAll()) {
            sb.append("[UserId=").append(user.getUserId()).append(", ")
                    .append("Name=").append(user.getName()).append(", ")
                    .append("Email=").append(user.getEmail()).append("]")
                    .append("\n");
        }

        ModelAndView mav = new ModelAndView(ResourceUtils.getStaticFile("/user/user-list.html"), HttpStatusCode.OK);
        mav.add("userList", sb.toString());
        return mav;
    }
}
