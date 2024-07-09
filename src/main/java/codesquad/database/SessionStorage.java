package codesquad.database;

import codesquad.model.User;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStorage {

    private static final Map<String, String> STORAGE = new ConcurrentHashMap<>();

    public static String store(User user) {
        String sid = UUID.randomUUID().toString();
        STORAGE.put(sid, user.getUserId());
        return sid;
    }
}
