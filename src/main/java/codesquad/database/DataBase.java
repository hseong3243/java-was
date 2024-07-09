package codesquad.database;

import codesquad.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DataBase {

    private static final Map<String, User> DATA = new ConcurrentHashMap<>();

    public static void addUser(User user) {
        DATA.put(user.getUserId(), user);
    }

    public static Optional<User> findUserByUserId(String userId) {
        return Optional.ofNullable(DATA.get(userId));
    }
}
