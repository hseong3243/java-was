package codesquad.application.database;

import codesquad.application.model.User;
import java.util.Optional;

public interface SessionStorage {
    String store(User user);

    Optional<String> findLoginUser(String sessionId);
}
