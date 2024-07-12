package codesquad.fixture;

import codesquad.application.model.User;

public class UserFixture {

    public static final String USER_ID = "tester";
    public static final String PASSWORD = "password";
    public static final String NAME = "테스터";
    public static final String EMAIL = "tester@tester.com";

    public static User user() {
        return User.create(USER_ID, PASSWORD, NAME, EMAIL);
    }
}
