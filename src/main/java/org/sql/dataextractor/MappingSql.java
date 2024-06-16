package org.sql.dataextractor;

import org.sql.patternsearch.RegexUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static org.sql.patternsearch.RegexUtils.findMatch;
import static org.sql.patternsearch.RegexUtils.findMatches;

public class MappingSql {
    private MappingSql(){}
    private static final String VALUES = "values";
    private static final String COLUMNS = "columns";
    static List<Map<String, Object>> processStatement(String element){
        Map<String, List<String>> keyAndValue = extractKeyAndValue(element);
        return convertToMappedRecords(keyAndValue.get(VALUES), keyAndValue.get(COLUMNS));
    }

    static List<Map<String, Object>> processStatement(String element, List<String> inserts,
                                                       List<Map<String,String>> foreignKeys){
        var valuesMapped = processStatement(element);

        foreignKeys.forEach(foreignKey ->{
            String tableReference=foreignKey.get("table_reference");
            String key = foreignKey.get("foreign_key");
            String reference = getReference(inserts, tableReference);

            var valuesForeignKey = processStatement(reference);

            valuesMapped.stream()
                    .filter(value -> value.containsKey(key))
                    .forEach(value -> {
                        value.remove(key);
                        int index = valuesMapped.indexOf(value);
                        if(index < valuesForeignKey.size())
                            value.put(tableReference, valuesForeignKey.get(index));
                    });
        });
        return valuesMapped;
    }
    static String getReference(List<String> inserts, String tableReference) {
        return inserts.stream().filter(value -> {
            String tableName = findMatch(value, RegexExtractor.TABLE_NAME,1);
            return tableReference.equals(tableName);
        }).findFirst().orElse(null);
    }

    private static List<Map<String, Object>> convertToMappedRecords(List<String> listValues, List<String> columnsNames) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        listValues.forEach(value -> {
            List<String> columnValues = findMatches(value, RegexExtractor.INSERT_COLUMN_VALUES, 1);
            Map<String, Object> valueMap = mapColumnValues(columnValues, columnsNames);
            resultList.add(valueMap);
        });

        return resultList;
    }

    private static Map<String, Object> mapColumnValues(List<String> columnValues, List<String> columnsNames) {
        Map<String, Object> valueMap = new HashMap<>();

        for (int index = 0; index < columnValues.size(); index++) {
            String value = columnValues.get(index).replace("'", "");
            String column = columnsNames.get(index);
            Matcher matcher = RegexUtils.getMatcher(value, "[\\d.]+(?![\\w\\s]+)");

            assert matcher != null;

            if(matcher.find()) {
                try {
                    int valueInt = Integer.parseInt(value);
                    valueMap.put(column, valueInt);
                }catch (NumberFormatException ex){
                    Double valueDouble = Double.parseDouble(value);
                    valueMap.put(column, valueDouble);
                }
                continue;
            }

            switch (value){
                case "NULL" -> valueMap.put(column, null);
                case "FALSE" -> valueMap.put(column, false);
                case "TRUE" -> valueMap.put(column, true);
                default -> valueMap.put(column, value);
            }
        }
        return valueMap;
    }
    private static Map<String, List<String>> extractKeyAndValue(String element) {
        Map<String, List<String>> map = new HashMap<>();
        List<String> values = findMatches(element, RegexExtractor.INSERT_DATA, 1);
        final List<String> columns = findMatches(values.getFirst(), RegexExtractor.INSERT_COLUMN_NAME);

        values.removeFirst();
        map.put(COLUMNS, columns);
        map.put(VALUES, values);
        return map;
    }
}
