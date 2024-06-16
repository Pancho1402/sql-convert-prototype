package dataextractor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sql.dataextractor.SQLExtractor;

import java.util.List;

class SQLExtractorTest {
    @Test
    void testQueryToMapped(){
        final String expected = "{orders=[{customer_id=1, order_id=101}, {customer_id=2, order_id=102}], customers=[{customer_name=John Doe, customer_id=1}, {customer_name=Jane Smith, customer_id=2}]}";
        SQLExtractor extractor = new SQLExtractor.ExtractData()
                .queryInput(SQL_QUERY)
                .extract();
        final String response = extractor.extractAndProcessData().toString();
        Assertions.assertEquals(expected, response);
    }
    @Test
    void testJoinQueryToMapped(){
        final String expectedJoin = "{orders=[{customers={customer_name=John Doe, customer_id=1}, order_id=101}, {customers={customer_name=Jane Smith, customer_id=2}, order_id=102}], customers=[{customer_name=John Doe, customer_id=1}, {customer_name=Jane Smith, customer_id=2}]}";
        SQLExtractor extractor = new SQLExtractor.ExtractData()
                .queryInput(SQL_QUERY)
                .isMappingJoin(true)
                .extract();
        final String responseJoin = extractor.extractAndProcessData().toString();
        Assertions.assertEquals(expectedJoin, responseJoin);
    }
    @Test
    void testQueryByFirstName(){
        final String expectedOrders = "{orders=[{customer_id=1, order_id=101}, {customer_id=2, order_id=102}]}";
        SQLExtractor extractor = new SQLExtractor.ExtractData()
                .queryInput(SQL_QUERY)
                .byTableNames(List.of("orders"))
                .extract();

        final String responseJoin = extractor.extractAndProcessData().toString();

        Assertions.assertEquals(expectedOrders, responseJoin);
    }
    @Test
    void testQueryByFirstNameOnJoin(){
        final String expectedOrders = "{orders=[{customers={customer_name=John Doe, customer_id=1}, order_id=101}, {customers={customer_name=Jane Smith, customer_id=2}, order_id=102}]}";
        SQLExtractor extractor = new SQLExtractor.ExtractData()
                .queryInput(SQL_QUERY)
                .byTableNames(List.of("orders"))
                .isMappingJoin(true)
                .extract();

        final String responseJoin = extractor.extractAndProcessData().toString();

        Assertions.assertEquals(expectedOrders, responseJoin);
    }
    @Test
    void testQueryByNames(){
        final String expected = "{orders=[{customer_id=1, order_id=101}, {customer_id=2, order_id=102}], customers=[{customer_name=John Doe, customer_id=1}, {customer_name=Jane Smith, customer_id=2}]}";
        SQLExtractor extractor = new SQLExtractor.ExtractData()
                .queryInput(SQL_QUERY)
                .byTableNames(List.of("orders","customers"))
                .extract();

        final String responseJoin = extractor.extractAndProcessData().toString();

        Assertions.assertEquals(expected, responseJoin);
    }
    @Test
    void testQueryByNamesOnJoin(){
        final String expectedJoin = "{orders=[{customers={customer_name=John Doe, customer_id=1}, order_id=101}, {customers={customer_name=Jane Smith, customer_id=2}, order_id=102}], customers=[{customer_name=John Doe, customer_id=1}, {customer_name=Jane Smith, customer_id=2}]}";
        SQLExtractor extractor = new SQLExtractor.ExtractData()
                .queryInput(SQL_QUERY)
                .byTableNames(List.of("orders","customers"))
                .isMappingJoin(true)
                .extract();

        final String responseJoin = extractor.extractAndProcessData().toString();

        Assertions.assertEquals(expectedJoin, responseJoin);
    }
    @Test
    void testQueryByCustomerOnJoin(){
        String expectedJoin = "{customers=[{customer_name=John Doe, customer_id=1}, {customer_name=Jane Smith, customer_id=2}]}";
        SQLExtractor extractor = new SQLExtractor.ExtractData()
                .queryInput(SQL_QUERY)
                .byTableNames(List.of("customers"))
                .isMappingJoin(true)
                .extract();

        final String responseJoin = extractor.extractAndProcessData().toString();

        Assertions.assertEquals(expectedJoin, responseJoin);
    }
    static final String SQL_QUERY = "CREATE TABLE customers (\n" +
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
