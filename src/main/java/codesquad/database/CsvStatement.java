package codesquad.database;

import codesquad.database.parser.DeleteParser;
import codesquad.database.parser.InsertParser;
import codesquad.database.parser.SqlParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvStatement implements Statement {

    private final CsvConnection connection;

    public CsvStatement(CsvConnection csvConnection) {
        this.connection = csvConnection;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        // SQL 파싱
        SqlParser parser = new SqlParser(sql);
        String tableName = parser.getTableName();
        List<String> columns = parser.getColumns();
        String whereClause = parser.getWhereClause();

        // CSV 파일 경로 가져오기
        String csvFilePath = connection.getFilePath(tableName);

        try {
            return selectFromCsv(csvFilePath, columns, whereClause);
        } catch (IOException e) {
            throw new SQLException("Error reading CSV file", e);
        }
    }
    private ResultSet selectFromCsv(String csvFilePath, List<String> columns, String whereClause) throws IOException, SQLException {
        List<String[]> data = new ArrayList<>();
        String[] headers;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            headers = reader.readLine().split(",");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (matchesWhereClause(headers, values, whereClause)) {
                    data.add(values);
                }
            }
        }

        List<Integer> columnIndices = getColumnIndices(headers, columns);
        return new CsvResultSet(data, columnIndices, headers);
    }

    private boolean matchesWhereClause(String[] headers, String[] values, String whereClause) {
        // WHERE 절 처리 로직 (간단한 구현)
        if (whereClause.isEmpty()) {
            return true;
        }
        String[] parts = whereClause.split("=");
        if (parts.length != 2) {
            return true;  // 복잡한 조건은 무시
        }
        String columnName = parts[0].trim();
        String value = parts[1].trim().replace("'", "").replace("\"", "");

        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(columnName)) {
                return values[i].trim().equalsIgnoreCase(value);
            }
        }
        return false;
    }


    private List<Integer> getColumnIndices(String[] headers, List<String> columns) {
        List<Integer> indices = new ArrayList<>();
        if (columns.contains("*")) {
            for (int i = 0; i < headers.length; i++) {
                indices.add(i);
            }
        } else {
            for (String column : columns) {
                for (int i = 0; i < headers.length; i++) {
                    if (headers[i].equalsIgnoreCase(column)) {
                        indices.add(i);
                        break;
                    }
                }
            }
        }
        return indices;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        if (sql.startsWith("insert")) {
            return executeInsert(sql);
        } else if (sql.startsWith("delete")) {
            return executeDelete(sql);
        }
        throw new SQLException("Only INSERT and DELETE statements are currently supported");
    }

    private int executeInsert(String sql) throws SQLException {
        InsertParser parser = new InsertParser(sql);
        String tableName = parser.getTableName();
        List<String> values = parser.getValues();

        String csvFilePath = connection.getFilePath(tableName);

        try {
            return insertIntoCsv(csvFilePath, values);
        } catch (IOException e) {
            throw new SQLException("Error writing to CSV file", e);
        }
    }

    private int insertIntoCsv(String csvFilePath, List<String> values) throws IOException, SQLException {
        // CSV 파일의 헤더 읽기
        List<String> headers;
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            headers = Arrays.asList(reader.readLine().split(","));
        }

        // 값의 개수가 헤더의 개수와 일치하는지 확인
        if (values.size() != headers.size()) {
            throw new SQLException("Number of values does not match number of columns");
        }

        // 새 행을 CSV 파일에 추가
        try (FileWriter fw = new FileWriter(csvFilePath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(String.join(",", values));
        }

        return 1; // 삽입된 행의 수 반환
    }

    private int executeDelete(String sql) throws SQLException {
        DeleteParser parser = new DeleteParser(sql);
        String tableName = parser.getTableName();
        String whereClause = parser.getWhereClause();

        String csvFilePath = connection.getFilePath(tableName);

        try {
            return deleteFromCsv(csvFilePath, whereClause);
        } catch (IOException e) {
            throw new SQLException("Error manipulating CSV file", e);
        }
    }

    private int deleteFromCsv(String csvFilePath, String whereClause) throws IOException, SQLException {
        List<String> remainingLines = new ArrayList<>();
        int deletedRows = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String header = reader.readLine();
            remainingLines.add(header);  // 헤더 유지

            String[] headerColumns = header.split(",");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (!matchesDeleteWhereClause(headerColumns, values, whereClause)) {
                    remainingLines.add(line);
                } else {
                    deletedRows++;
                }
            }
        }

        // 수정된 내용을 CSV 파일에 쓰기
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath))) {
            for (String line : remainingLines) {
                writer.println(line);
            }
        }

        return deletedRows;
    }

    private boolean matchesDeleteWhereClause(String[] headers, String[] values, String whereClause) {
        if (whereClause.isEmpty()) {
            return true;  // WHERE 절이 없으면 모든 행 삭제
        }

        String[] parts = whereClause.split("=");
        if (parts.length != 2) {
            return false;  // 복잡한 조건은 무시
        }

        String columnName = parts[0].trim();
        String value = parts[1].trim().replace("'", "").replace("\"", "");

        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(columnName)) {
                return values[i].trim().equalsIgnoreCase(value);
            }
        }

        return false;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {

    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {

    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {

    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {

    }

    @Override
    public void cancel() throws SQLException {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {

    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public void addBatch(String sql) throws SQLException {

    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {

    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
