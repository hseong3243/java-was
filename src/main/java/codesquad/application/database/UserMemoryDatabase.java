package codesquad.application.database;

import codesquad.application.model.User;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserMemoryDatabase implements UserDatabase {
    private final Map<String, User> database = new ConcurrentHashMap<>();

    @Override
    public void addUser(User user) {
        database.put(user.getUserId(), user);
    }

    @Override
    public Optional<User> findUserByUserId(String userId) {
        return Optional.ofNullable(database.get(userId));
    }

    @Override
    public List<User> findAll() {
        return database.values().stream().sorted(Comparator.comparing(User::getName)).toList();
    }
}
