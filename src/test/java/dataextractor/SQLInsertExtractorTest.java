package dataextractor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sql.dataextractor.RegexExtractor;
import org.sql.dataextractor.SQLInsertExtractor;
import org.sql.patternsearch.RegexUtils;

import java.util.HashMap;
import java.util.Map;

public class SQLInsertExtractorTest {

    private final SQLInsertExtractor insert = new SQLInsertExtractor();

    @Test
    void testResultNoJoinQuery(){
        final String expected = "{orders=[{customer_id=1, order_id=101}, {customer_id=2, order_id=102}], customers=[{customer_name=John Doe, customer_id=1}, {customer_name=Jane Smith, customer_id=2}]}";
        final String response = insert.extractDataToMap(SQL_QUERY, false).toString();

        Assertions.assertEquals(expected, response);
    }

    @Test
    void testResultJoinQuery(){
        final String expectedJoin = "{orders=[{customers={customer_name=John Doe, customer_id=1}, order_id=101}, {customers={customer_name=Jane Smith, customer_id=2}, order_id=102}], customers=[{customer_name=John Doe, customer_id=1}, {customer_name=Jane Smith, customer_id=2}]}";
        final String responseJoin = insert.extractDataToMap(SQL_QUERY, true).toString();

        Assertions.assertEquals(expectedJoin, responseJoin);
    }
    @Test
    void testValuesInForeignKey(){
        String table = "orders (\n" +
                "    order_id INT PRIMARY KEY,\n" +
                "    customer_id INT,\n" +
                "    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)\n" +
                ");\n" ;
        String tableName = RegexUtils.findMatch(table, RegexExtractor.TABLE_NAME,1);
        Map<String, Map<String, String>> expected = new HashMap<>();
        Map<String, String> foreignKey = new HashMap<>();

        foreignKey.put("foreign_key", "customer_id");
        foreignKey.put("table_reference", "customers");
        foreignKey.put("column_reference", "customer_id");

        expected.put(tableName, foreignKey);

        Assertions.assertEquals("orders", tableName);
        Assertions.assertEquals(expected, insert.getForeignKey(table));

    }
    public static final String SQL_QUERY = "CREATE TABLE customers (\n" +
            "    customer_id INT PRIMARY KEY,\n" +
            "    customer_name VARCHAR(50)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE orders (\n" +
            "    order_id INT PRIMARY KEY,\n" +
            "    customer_id INT,\n" +
            "    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)\n" +
            ");\n" +
            "INSERT INTO customers (customer_id, customer_name) VALUES\n" +
            "(1, 'John Doe'),\n" +
            "(2, 'Jane Smith');\n" +
            "\n" +
            "INSERT INTO orders (order_id, customer_id) VALUES\n" +
            "(101, 1),\n" +
            "(102, 2);\n";
}
