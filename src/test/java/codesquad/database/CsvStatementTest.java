package codesquad.database;

import static org.assertj.core.api.Assertions.assertThat;

import codesquad.application.model.User;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CsvStatementTest {

    private CsvDriver csvDriver;
    private Connection con;
    private Statement stmt;

    @BeforeEach
    void setUp() throws SQLException {
        csvDriver = new CsvDriver();
        con = csvDriver.connect("jdbc:csv:testdb", null);
        stmt = con.createStatement();

        stmt.executeUpdate("delete from user");
        stmt.executeUpdate("delete from article");
    }

    private static User userMapper(ResultSet rs) throws SQLException {
        User user = null;
        if (rs.next()) {
            String userId = rs.getString("userId");
            String name = rs.getString("name");
            String password = rs.getString("password");
            String email = rs.getString("email");
            user = User.create(userId, password, name, email);
        }
        return user;
    }

    @Nested
    @DisplayName("executeQuery 호출 시")
    class ExecuteQueryTest {

        @Test
        @DisplayName("user 테이블 select 문 실행 시")
        void selectUser() throws SQLException {
            //given
            String insert = "insert into user values ('userId', 'username', 'password', 'email@email.com')";
            stmt.executeUpdate(insert);
            String sql = "select * from user where userId = 'userId'";

            //when
            ResultSet rs = stmt.executeQuery(sql);

            //then
            User user = userMapper(rs);
            assertThat(user).isNotNull();
            assertThat(user.getUserId()).isEqualTo("userId");
            assertThat(user.getName()).isEqualTo("username");
            assertThat(user.getPassword()).isEqualTo("password");
            assertThat(user.getEmail()).isEqualTo("email@email.com");
        }

        @Test
        @DisplayName("article 테이블 select 문 실행 시")
        void selectArticle() throws SQLException {
            //given
            String insert = "insert into article values ('1', 'test', 'content', 'test', 'imagefile.png')";
            stmt.executeUpdate(insert);
            String sql = "select * from article where articleId = '1'";

            //when
            ResultSet rs = stmt.executeQuery(sql);

            //then
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("articleId")).isEqualTo("1");
            assertThat(rs.getString("title")).isEqualTo("test");
            assertThat(rs.getString("content")).isEqualTo("content");
            assertThat(rs.getString("userId")).isEqualTo("test");
            assertThat(rs.getString("imageFilename")).isEqualTo("imagefile.png");
        }

    }

    @Nested
    @DisplayName("executeUpdate 호출 시")
    class ExecuteUpdateTest {

        @Nested
        @DisplayName("insert 문 실행 시")
        class InsertTest {

            @Test
            @DisplayName("유저 데이터베이스에 데이터를 저장한다.")
            void insertUser() throws SQLException {
                //given
                String sql = "insert into user values ('insert', 'insert', 'insert', 'insert@test.com')";

                //when
                stmt.executeUpdate(sql);

                //then
                ResultSet rs = stmt.executeQuery("select * from user where userId = 'insert'");
                User user = userMapper(rs);
                assertThat(user).isNotNull();
                assertThat(user.getUserId()).isEqualTo("insert");
                assertThat(user.getPassword()).isEqualTo("insert");
                assertThat(user.getName()).isEqualTo("insert");
                assertThat(user.getEmail()).isEqualTo("insert@test.com");
            }

            @Test
            @DisplayName("게시글 데이터베이스에 데이터를 저장한다.")
            void insertArticle() throws SQLException {
                //given
                String sql = "insert into article values ('5', 'test', 'content', 'test', 'imagefile.png')";

                //when
                stmt.executeUpdate(sql);

                //then
                ResultSet rs = stmt.executeQuery("select * from article where articleId = '5'");
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString("articleId")).isEqualTo("5");
                assertThat(rs.getString("title")).isEqualTo("test");
                assertThat(rs.getString("content")).isEqualTo("content");
                assertThat(rs.getString("userId")).isEqualTo("test");
                assertThat(rs.getString("imageFilename")).isEqualTo("imagefile.png");
            }
        }

        @Nested
        @DisplayName("delete 문 실행 시")
        class DeleteTest {

            @Test
            @DisplayName("유저 데이터베이스에서 데이터를 삭제한다.")
            void deleteUser() throws SQLException {
                //given
                String insert = "insert into user values ('delete', 'delete', 'delete', 'delete@test.com')";
                stmt.executeUpdate(insert);
                String sql = "delete from user where userId = 'delete'";

                //when
                stmt.executeUpdate(sql);

                //then
                ResultSet rs = stmt.executeQuery("select * from user where userId = 'delete'");
                assertThat(rs.next()).isFalse();
            }

            @Test
            @DisplayName("게시글 데이터베이스에서 데이터를 삭제한다.")
            void deleteArticle() throws SQLException {
                //given
                String insert = "insert into article values ('1', 'testTitle', 'testContent', 'testUser', 'imagefile.png')";
                stmt.executeUpdate(insert);
                String sql = "delete from article where articleId = '1'";

                //when
                stmt.executeUpdate(sql);

                //then
                ResultSet rs = stmt.executeQuery("select * from article where articleId = '1'");
                assertThat(rs.next()).isFalse();
            }
        }
    }
}
