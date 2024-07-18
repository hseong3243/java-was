package codesquad.application.database.memory;

import codesquad.application.database.SessionStorage;
import codesquad.application.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionMemoryStorage implements SessionStorage {

    private final Map<String, String> storage = new ConcurrentHashMap<>();

    @Override
    public String store(User user) {
        List<String> sessionIds = new ArrayList<>();
        storage.forEach((key, value) -> {
            if (value.equals(user.getUserId())) {
                sessionIds.add(key);
            }
        });
        sessionIds.forEach(storage::remove);

        String sid = UUID.randomUUID().toString();
        storage.put(sid, user.getUserId());
        return sid;
    }

    @Override
    public Optional<String> findLoginUser(String sessionId) {
        return Optional.ofNullable(storage.get(sessionId));
    }
}
