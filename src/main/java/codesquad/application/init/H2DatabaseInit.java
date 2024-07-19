package codesquad.application.init;

import codesquad.application.util.DBConnectionUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H2DatabaseInit implements DatabaseInit {

    private static final Logger log = LoggerFactory.getLogger(H2DatabaseInit.class);

    public void init() {
        Connection con = DBConnectionUtils.getConnection();
        String createUserSQL = "CREATE TABLE if not exists users (" +
                "user_id varchar(100) PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "email VARCHAR(100)," +
                "password varchar(255) not null" +
                ")";
        String createArticleSQL = "create table if not exists article ("
                + "article_id int auto_increment primary key,"
                + "title varchar(100) not null,"
                + "content varchar(100) not null,"
                + "user_id varchar(100),"
                + "image_filename varchar(255),"
                + " foreign key (user_id) references users (user_id))";
        String createSessionStorageSQL = "create table if not exists sessions ("
                + "session_id varchar(100) primary key,"
                + "user_id varchar(100) not null,"
                + "foreign key (user_id) references users (user_id))";

        PreparedStatement userPstmt = null;
        PreparedStatement articlePstmt = null;
        PreparedStatement sessionPstmt = null;
        try  {
            userPstmt = con.prepareStatement(createUserSQL);
            articlePstmt = con.prepareStatement(createArticleSQL);
            sessionPstmt = con.prepareStatement(createSessionStorageSQL);
            userPstmt.executeUpdate();
            articlePstmt.executeUpdate();
            sessionPstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("데이터베이스 초기화에 실패했습니다.", e);
            throw new RuntimeException(e);
        } finally {
            DBConnectionUtils.closeConnection(null, userPstmt, null);
            DBConnectionUtils.closeConnection(null, articlePstmt, null);
            DBConnectionUtils.closeConnection(null, sessionPstmt, null);
            DBConnectionUtils.closeConnection(con, null, null);
        }
    }
}
