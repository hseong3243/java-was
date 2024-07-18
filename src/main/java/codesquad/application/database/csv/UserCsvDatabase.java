package codesquad.application.database.csv;

import codesquad.application.database.UserDatabase;
import codesquad.application.model.User;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserCsvDatabase implements UserDatabase {
    @Override
    public void addUser(User user) {
        String sql = "insert into user values (''{0}'', ''{1}'', ''{2}'', ''{3}'')";
        sql = MessageFormat.format(sql,
                user.getUserId(),
                user.getPassword(), user.getPassword(), user.getEmail());

        Connection con = null;
        Statement stmt = null;
        try {
            con = CSVConnectionUtil.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CSVConnectionUtil.closeConnection(con, stmt, null);
        }
    }

    @Override
    public Optional<User> findUserByUserId(String userId) {
        String sql = "select * from user where userId = ''{0}''";
        sql = MessageFormat.format(sql, userId);

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = CSVConnectionUtil.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                User user = User.create(
                        rs.getString("userId"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            CSVConnectionUtil.closeConnection(con, stmt, rs);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "select * from user";

        List<User> users = new ArrayList<>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = CSVConnectionUtil.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                User user = User.create(
                        rs.getString("userId"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
