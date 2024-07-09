package codesquad.model;

import java.util.NoSuchElementException;

public class User {

    private final String userId;
    private final String password;
    private final String name;
    private final String email;

    private User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public static User create(String userId, String password, String name, String email) {
        return new User(userId, password, name, email);
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void validatePassword(String password) {
        if(this.password.equals(password)) {
            return;
        }
        throw new NoSuchElementException("존재하지 않는 사용자입니다.");
    }
}
