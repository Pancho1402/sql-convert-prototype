package org.sql.dataextractor;

import org.sql.patternsearch.RegexUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinExtractor {
    private JoinExtractor(){}
    public static Map<String, List<Map<String, String>>> extractForeignKeys(List<String> dataCreate){
        Map<String, List<Map<String, String>>> foreignKey = new HashMap<>();

        dataCreate.stream()
                .filter(element->!getForeignKey(element).isEmpty())
                .forEach(element -> foreignKey.putAll(getForeignKey(element)));
        return foreignKey;
    }
    public static Map<String, List<Map<String, String>>> getForeignKey(String element){
        final String tableName = RegexUtils.findMatch(element, RegexExtractor.TABLE_NAME,1);
        Map<String, List<Map<String, String>>> map = new HashMap<>();

        List<String> matchesForeignKeys =  RegexUtils.findMatches(element, RegexExtractor.FOREIGN_KEY);
        List<Map<String, String>> responses = new ArrayList<>();

        matchesForeignKeys.forEach(value ->{
            Map<String, String> foreignKey = new HashMap<>();

            foreignKey.put("foreign_key", valuesInForeignKey(value, 1));
            foreignKey.put("table_reference", valuesInForeignKey(value, 2));
            foreignKey.put("column_reference", valuesInForeignKey(value, 3));

            responses.add(foreignKey);
        });

        map.put(tableName, responses);
        return map;
    }
    public static String valuesInForeignKey(String element, int groupIndex){
        return RegexUtils.findMatch(element, RegexExtractor.FOREIGN_KEY, groupIndex);
    }
}
