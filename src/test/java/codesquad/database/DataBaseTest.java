package codesquad.database;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.model.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DataBaseTest {

    @Nested
    @DisplayName("addUser 호출 시")
    class AddUserTest {

        @Test
        @DisplayName("사용자가 저장된다.")
        void test() {
            //given
            User user = User.create("userId", "password", "name", "email@email.com");

            //when
            DataBase.addUser(user);

            //then
            Optional<User> optionalUser = DataBase.findUserByUserId("userId");
            assertThat(optionalUser).isPresent().get().satisfies(findUser -> {
                assertThat(findUser.getUserId()).isEqualTo("userId");
                assertThat(findUser.getPassword()).isEqualTo("password");
                assertThat(findUser.getName()).isEqualTo("name");
                assertThat(findUser.getEmail()).isEqualTo("email@email.com");
            });
        }
    }
}
