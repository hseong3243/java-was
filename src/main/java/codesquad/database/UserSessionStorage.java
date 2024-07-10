package codesquad.database;

import codesquad.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserSessionStorage {

    private final Map<String, String> storage = new ConcurrentHashMap<>();

    public String store(User user) {
        String sid = UUID.randomUUID().toString();
        storage.put(sid, user.getUserId());
        return sid;
    }

    public Optional<String> findLoginUser(String sessionId) {
        return Optional.ofNullable(storage.get(sessionId));
    }

    public boolean isValid(String sessionId) {
        return findLoginUser(sessionId).isPresent();
    }
}
