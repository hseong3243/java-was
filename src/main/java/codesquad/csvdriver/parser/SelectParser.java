package codesquad.csvdriver.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectParser {

    private String sql;
    private String tableName;
    private List<String> columns;
    private String whereClause;

    public SelectParser(String sql) {
        this.sql = sql.toLowerCase();
        parse();
    }

    private void parse() {
        String[] tokens = sql.split("\\s+");
        if (!tokens[0].equals("select")) {
            throw new IllegalArgumentException("Only SELECT statements are supported");
        }

        int fromIndex = indexOf(tokens, "from");
        int whereIndex = indexOf(tokens, "where");

        // Parse columns
        columns = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(tokens, 1, fromIndex)));
        columns.replaceAll(s -> s.replace(",", ""));

        // Parse table name
        tableName = tokens[fromIndex + 1];

        // Parse WHERE clause
        if (whereIndex != -1) {
            whereClause = String.join(" ", Arrays.copyOfRange(tokens, whereIndex + 1, tokens.length));
        } else {
            whereClause = "";
        }
    }

    private int indexOf(String[] arr, String target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public String getWhereClause() {
        return whereClause;
    }
}
