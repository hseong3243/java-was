package codesquad.database;

public class DeleteParser {
    private String sql;
    private String tableName;
    private String whereClause;

    public DeleteParser(String sql) {
        this.sql = sql.trim();
        parse();
    }

    private void parse() {
        // "DELETE FROM table_name WHERE condition" 형식 파싱
        String[] parts = sql.split("\\s+", 5);
        if (parts.length < 3 || !parts[0].equalsIgnoreCase("delete") || !parts[1].equalsIgnoreCase("from")) {
            throw new IllegalArgumentException("Invalid DELETE statement");
        }

        tableName = parts[2];

        // WHERE 절 파싱
        if (parts.length > 3 && parts[3].equalsIgnoreCase("where")) {
            whereClause = parts[4];
        } else {
            whereClause = "";
        }
    }

    public String getTableName() {
        return tableName;
    }

    public String getWhereClause() {
        return whereClause;
    }
}
