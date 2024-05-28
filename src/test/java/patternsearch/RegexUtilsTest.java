package patternsearch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sql.patternsearch.RegexUtils;

import java.util.ArrayList;
import java.util.List;

public class RegexUtilsTest {
    public static final String SQL_NOT_JOIN = "-- Inserción en TableName1\n" +
            "INSERT INTO TableName1 (name, edad, direccion, email)\n" +
            "VALUES ('Juan', 30, 'Calle 123', 'juan@example.com'),\n" +
            "       ('María', 25, 'Avenida 456', 'maria@example.com');\n" +
            "\n" +
            "-- Inserción en TableName4\n" +
            "INSERT INTO TableName4 (name, edad, direccion, telefono)\n" +
            "VALUES ('Pedro', 35, 'Carrera 789', '123-456-7890'),\n" +
            "       ('Ana', 28, 'Plaza 101', '987-654-3210');\n";
    private static final String REGEX_INSERT = "INSERT\\sINTO\\s(.+\\((?:(?:.\\s?)+)\\));";
    private static final String REGEX_TABLE_NAME = "(\\w+)\\s?\\(\\w+";
    private static final String REGEX_DATA = "\\((.+)+\\)";
    private static final String REGEX_COLUMN_NAME = "\\w+";
    private static final String REGEX_COLUMN_VALUES = "('(?:[\\D\\d]?(?:\\W?\\w)+)+'|\\w+)";

    @Test
    void AmountInsertsTest(){
        final int expected = 2;
        final List<String> response = RegexUtils.findMatches(SQL_NOT_JOIN, REGEX_INSERT);

        Assertions.assertEquals(expected, response.size());
    }
    @Test
    void firstInsertObjectTest(){
        final String expected = "INSERT INTO TableName1 (name, edad, direccion, email)\n" +
                "VALUES ('Juan', 30, 'Calle 123', 'juan@example.com'),\n" +
                "       ('María', 25, 'Avenida 456', 'maria@example.com');";
        final String response = RegexUtils.findMatch(SQL_NOT_JOIN, REGEX_INSERT);

        Assertions.assertEquals(expected, response);
    }
    @Test
    void allInsertObjectsTest(){
        List<String> expected = new ArrayList<String>();
        expected.add("INSERT INTO TableName1 (name, edad, direccion, email)\n" +
                "VALUES ('Juan', 30, 'Calle 123', 'juan@example.com'),\n" +
                "       ('María', 25, 'Avenida 456', 'maria@example.com');");

        expected.add("INSERT INTO TableName4 (name, edad, direccion, telefono)\n" +
                "VALUES ('Pedro', 35, 'Carrera 789', '123-456-7890'),\n" +
                "       ('Ana', 28, 'Plaza 101', '987-654-3210');");
        final List<String> response = RegexUtils.findMatches(SQL_NOT_JOIN, REGEX_INSERT);

        Assertions.assertEquals(expected, response);
    }

    @Test
    void tableNameTest(){
        final String expected = "TableName1";
        final String response = RegexUtils.findMatch(SQL_NOT_JOIN, REGEX_TABLE_NAME, 1);

        Assertions.assertEquals(expected, response);
    }
    @Test
    void allDataTest(){
        List<String> expected = new ArrayList<String>();
        final List<String> response = RegexUtils.findMatches(SQL_NOT_JOIN, REGEX_DATA,1);

        expected.add("name, edad, direccion, email");
        expected.add("'Juan', 30, 'Calle 123', 'juan@example.com'");
        expected.add("'María', 25, 'Avenida 456', 'maria@example.com'");
        expected.add("name, edad, direccion, telefono");
        expected.add("'Pedro', 35, 'Carrera 789', '123-456-7890'");
        expected.add("'Ana', 28, 'Plaza 101', '987-654-3210'");

        Assertions.assertEquals(expected, response);
    }
    @Test
    void columnNameTest(){
        String columns = "name, edad, direccion, email";

        final List<String> expected = List.of("name", "edad", "direccion","email");
        final List<String> response = RegexUtils.findMatches(columns, REGEX_COLUMN_NAME);

        Assertions.assertEquals(expected, response);
    }
}