package codesquad.application.database;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.application.database.memory.SessionMemoryStorage;
import codesquad.application.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SessionStorageTest {

    private SessionMemoryStorage sessionStorage;

    @BeforeEach
    void setUp() {
        sessionStorage = new SessionMemoryStorage();
    }

    @Nested
    @DisplayName("store 호출 시")
    class StoreTest {

        @Test
        @DisplayName("sessionID를 반환한다.")
        void returnSessionId() {
            //given
            User user = User.create("userId", "password", "name", "email@email.com");

            //when
            String sessionId = sessionStorage.store(user);

            //then
            assertThat(sessionId).isNotBlank();
        }
    }
}
