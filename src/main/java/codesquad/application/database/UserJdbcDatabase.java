package codesquad.application.database;

import codesquad.application.model.User;
import codesquad.application.util.DBConnectionUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserJdbcDatabase implements UserDatabase {

    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO users (user_id, password, name, email) VALUES (?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = DBConnectionUtils.getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("SQL 예외 발생", e);
        } finally {
            DBConnectionUtils.closeConnection(con, pstmt, null);
        }
    }

    @Override
    public Optional<User> findUserByUserId(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBConnectionUtils.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = User.create(
                        rs.getString("user_id"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalArgumentException("SQL 예외 발생", e);
        } finally {
            DBConnectionUtils.closeConnection(con, pstmt, rs);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBConnectionUtils.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                User user = User.create(
                        rs.getString("user_id"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new IllegalArgumentException("SQL 예외 발생", e);
        } finally {
            DBConnectionUtils.closeConnection(con, pstmt, rs);
        }
    }
}
