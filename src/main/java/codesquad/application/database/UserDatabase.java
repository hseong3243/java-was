package codesquad.application.database;

import codesquad.application.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDatabase {
    void addUser(User user);

    Optional<User> findUserByUserId(String userId);

    List<User> findAll();
}
