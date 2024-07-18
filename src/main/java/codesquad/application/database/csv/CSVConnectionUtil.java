package codesquad.application.database.csv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CSVConnectionUtil {
    private static final String JDBC_URL = "jdbc:csv:realcsv";

    public static Connection getConnection() {
        try {
            Class.forName("codesquad.csvdriver.CsvDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            return DriverManager.getConnection(JDBC_URL);
        } catch (SQLException e) {
            throw new IllegalArgumentException("커넥션 획득에 실패했습니다.", e);
        }
    }

    public static void closeConnection(Connection con, Statement stmt, ResultSet rs) {
        if(con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if(stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
