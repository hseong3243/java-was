package codesquad.application.model;

public class Author {
    private final String userId;
    private final String name;

    public Author(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public static Author create(User user) {
        return new Author(user.getUserId(), user.getName());
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
