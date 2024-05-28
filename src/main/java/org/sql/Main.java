package org.sql;


import org.sql.dataextractor.SQLInsertExtractor;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final SQLInsertExtractor insert = new SQLInsertExtractor();
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

    public static void main(String[] args) {
        insert.extractDataToMap(SQL_QUERY, true);
    }

}