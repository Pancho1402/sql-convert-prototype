package dataextractor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sql.dataextractor.SQLExtractor;

class SQLExtractorTest {

    private final SQLExtractor insert = new SQLExtractor();

    @Test
    void testResultNoJoinQuery(){
        final String expected = "{orders=[{customer_id=1, order_id=101}, {customer_id=2, order_id=102}], customers=[{customer_name=John Doe, customer_id=1}, {customer_name=Jane Smith, customer_id=2}]}";
        final String response = insert.extractDataToMap(SQL_QUERY, false).toString();

        Assertions.assertEquals(expected, response);
    }

    @Test
    void testResultJoinQuery(){
        final String expectedJoin = "{orders=[{customers={customer_name=John Doe, customer_id=1}, order_id=101}, {customers={customer_name=Jane Smith, customer_id=2}, order_id=102}], customers=[{customer_name=John Doe, customer_id=1}, {customer_name=Jane Smith, customer_id=2}]}";
        final String responseJoin = insert.extractDataToMap(SQL_QUERY , true).toString();

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
    static final String INITIAL_SQL_SCRIPT  = "CREATE TABLE customers (\n" +
            "    customer_id INT PRIMARY KEY,\n" +
            "    customer_name VARCHAR(50)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE products (\n" +
            "    product_id INT PRIMARY KEY,\n" +
            "    product_name VARCHAR(50)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE orders (\n" +
            "    order_id INT PRIMARY KEY,\n" +
            "    reference_customer_id INT,\n" +
            "    reference_product_id INT,\n" +
            "    FOREIGN KEY (reference_customer_id) REFERENCES customers(customer_id),\n" +
            "    FOREIGN KEY (reference_product_id) REFERENCES products(product_id)\n" +
            ");\n" +
            "\n" +
            "INSERT INTO customers (customer_id, customer_name) VALUES\n" +
            "(1, 'John Doe'),\n" +
            "(2, 'Jane Smith');\n" +
            "\n" +
            "INSERT INTO products (product_id, product_name) VALUES\n" +
            "(1, 'Product A'),\n" +
            "(2, 'Product B');\n" +
            "\n" +
            "INSERT INTO orders (order_id, reference_customer_id, reference_product_id) VALUES\n" +
            "(101, 1, 1),\n" +
            "(102, 2, 2),\n" +
            "(103, 1, 2);\n";
}
