package codesquad.application.database.h2;

import codesquad.application.database.SessionStorage;
import codesquad.application.model.User;
import codesquad.application.util.DBConnectionUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class SessionJdbcStorage implements SessionStorage {

    @Override
    public String store(User user) {
        Connection con = DBConnectionUtils.getConnection();
        try {
            DBConnectionUtils.startTransaction(con);
            invalidate(con, user);
            String sessionId = save(con, user);
            con.commit();
            return sessionId;
        } catch (SQLException e) {
            DBConnectionUtils.rollback(con);
            throw new IllegalArgumentException("SQL 에러 발생",e);
        } finally {
            DBConnectionUtils.closeConnection(con, null, null);
        }
    }

    public void invalidate(Connection con, User user) {
        String sql = "delete from sessions where user_id = ?";

        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, user.getUserId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("SQL 에러 발생",e);
        } finally {
            DBConnectionUtils.closeConnection(null, pstmt, null);
        }
    }

    public String save(Connection con, User user) {
        String sql = "insert into sessions (session_id, user_id) values (?, ?)";

        String sessionId = UUID.randomUUID().toString();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, sessionId);
            pstmt.setString(2, user.getUserId());
            pstmt.executeUpdate();
            return sessionId;
        } catch (SQLException e) {
            throw new IllegalArgumentException("SQL 에러 발생",e);
        } finally {
            DBConnectionUtils.closeConnection(null, pstmt, null);
        }
    }

    @Override
    public Optional<String> findLoginUser(String sessionId) {
        String sql = "select * from sessions where session_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBConnectionUtils.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, sessionId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String userId = rs.getString("user_id");
                return Optional.of(userId);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalArgumentException("SQL 에러 발생",e);
        } finally {
            DBConnectionUtils.closeConnection(con, pstmt, rs);
        }
    }
}
