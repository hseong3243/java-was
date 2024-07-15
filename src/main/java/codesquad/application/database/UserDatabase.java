package codesquad.application.database;

import codesquad.application.model.User;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserDatabase {
    private final Map<String, User> database = new ConcurrentHashMap<>();

    public void addUser(User user) {
        database.put(user.getUserId(), user);
    }

    public Optional<User> findUserByUserId(String userId) {
        return Optional.ofNullable(database.get(userId));
    }

    public List<User> findAll() {
        return database.values().stream().sorted(Comparator.comparing(User::getName)).toList();
    }
}
