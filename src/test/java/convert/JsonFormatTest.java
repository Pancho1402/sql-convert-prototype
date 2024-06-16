package convert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sql.convert.JsonFormat;
import org.sql.dataextractor.SQLExtractor;

public class JsonFormatTest {
    @Test
    void jsonConvertTest(){
        var map =insert.extractAndProcessData();
        String expected = "{\"orders\":[{\"customers\":{\"customer_name\":\"John Doe\",\"customer_id\":1},\"order_id\":101},{\"customers\":{\"customer_name\":\"Jane Smith\",\"customer_id\":2},\"order_id\":102}],\"customers\":[{\"customer_name\":\"John Doe\",\"customer_id\":1},{\"customer_name\":\"Jane Smith\",\"customer_id\":2}]}";
        String actual = JsonFormat.jsonConvert(map);
        Assertions.assertEquals(expected, actual);
    }
    private SQLExtractor insert = new SQLExtractor.ExtractData()
            .queryInput(SQL_QUERY)
            .isMappingJoin(true)
            .extract();
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
