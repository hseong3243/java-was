package codesquad.csvdriver.parser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InsertParser {
    private String sql;
    private String tableName;
    private List<String> values;

    public InsertParser(String sql) {
        this.sql = sql.trim();
        parse();
    }

    private void parse() {
        // "INSERT INTO table_name VALUES (value1, value2, ...)" 형식 파싱
        String[] parts = sql.split("\\s+", 5);
        if (parts.length < 4 || !parts[0].equalsIgnoreCase("insert") || !parts[1].equalsIgnoreCase("into")) {
            throw new IllegalArgumentException("Invalid INSERT statement");
        }

        tableName = parts[2];

        // VALUES 부분 파싱
        String valuesString = parts[4];
        if (!valuesString.startsWith("(") || !valuesString.endsWith(")")) {
            throw new IllegalArgumentException("Invalid VALUES format");
        }

        valuesString = valuesString.substring(1, valuesString.length() - 1);
        values = Arrays.stream(valuesString.split(","))
                .map(String::trim)
                .map(this::removeQuotes)
                .collect(Collectors.toList());
    }

    private String removeQuotes(String value) {
        if ((value.startsWith("'") && value.endsWith("'")) ||
                (value.startsWith("\"") && value.endsWith("\""))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getValues() {
        return values;
    }
}
