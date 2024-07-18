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
            String sql = "select * from user where userId = 'suserId'";

            //when
            ResultSet rs = stmt.executeQuery(sql);

            //then
            User user = userMapper(rs);
            assertThat(user).isNotNull();
            assertThat(user.getUserId()).isEqualTo("suserId");
            assertThat(user.getName()).isEqualTo("sname");
            assertThat(user.getPassword()).isEqualTo("spassword");
            assertThat(user.getEmail()).isEqualTo("semail@email.com");
        }

        @Test
        @DisplayName("article 테이블 select 문 실행 시")
        void selectArticle() throws SQLException {
            //given
            String sql = "select * from article where articleId = '1'";

            //when
            ResultSet rs = stmt.executeQuery(sql);

            //then
            String imageFilename = "";
            String articleId = "";
            String title = "";
            String content = "";
            String userId = "";
            if (rs.next()) {
                imageFilename = rs.getString("imageFilename");
                articleId = rs.getString("articleId");
                title = rs.getString("title");
                content = rs.getString("content");
                userId = rs.getString("userId");
            }
            assertThat(articleId).isEqualTo("1");
            assertThat(title).isEqualTo("aTitle");
            assertThat(content).isEqualTo("aContent");
            assertThat(userId).isEqualTo("aUserId");
            assertThat(imageFilename).isEqualTo("aImageFilename");
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
                String sql = "insert into article values ('5', 'testTitle', 'testContent', 'testUser', 'imagefile.png')";

                //when
                stmt.executeUpdate(sql);

                //then
                ResultSet rs = stmt.executeQuery("select * from article where articleId = '5'");
                assertThat(rs.next()).isTrue();
                assertThat(rs.getString("articleId")).isEqualTo("5");
                assertThat(rs.getString("title")).isEqualTo("testTitle");
                assertThat(rs.getString("content")).isEqualTo("testContent");
                assertThat(rs.getString("userId")).isEqualTo("testUser");
                assertThat(rs.getString("imageFilename")).isEqualTo("imagefile.png");
            }
        }

        @Test
        @DisplayName("delete 문 실행 시")
        void delete() throws SQLException {
            //given
            CsvConnection csvConnection = new CsvConnection("jdbc:csv:file/user.csv");
            Statement statement = csvConnection.createStatement();
            statement.executeUpdate("insert into user values('delete', 'delete', 'delete', 'delete@test.com')");

            //when
            statement.executeUpdate("delete from user where userId = 'delete'");

            //then
            ResultSet rs = statement.executeQuery("select * from user where userId = 'delete'");
            User user = userMapper(rs);
            assertThat(user).isNull();
        }

    }
}
