package dataextractor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sql.dataextractor.JoinExtractor;
import org.sql.dataextractor.RegexExtractor;
import static org.sql.patternsearch.RegexUtils.findMatches;


class JoinExtractorTest {
    @Test
    void referenceForeignKeys(){
        var dataCreate = findMatches(SQL_QUERY, RegexExtractor.TABLE, 1);
        String expected = "{orders=[{column_reference=customer_id, foreign_key=reference_customer_id, table_reference=customers}, {column_reference=product_id, foreign_key=reference_product_id, table_reference=products}], customers=[], products=[]}";
        var foreignKey = JoinExtractor.extractForeignKeys(dataCreate);
        Assertions.assertEquals(expected, foreignKey.toString());
    }
    @Test
    void getForeignKeysTest(){
        var dataCreate = findMatches(SQL_QUERY, RegexExtractor.TABLE, 1);
        var foreignKey = JoinExtractor.getForeignKey(dataCreate.getLast());
        var expected = "[{column_reference=customer_id, foreign_key=reference_customer_id, table_reference=customers}, {column_reference=product_id, foreign_key=reference_product_id, table_reference=products}]";

        Assertions.assertEquals(expected, foreignKey.get("orders").toString());
    }

    static final String SQL_QUERY = "CREATE TABLE customers (\n" +
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
