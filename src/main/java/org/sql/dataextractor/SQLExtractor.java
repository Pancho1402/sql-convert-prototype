package org.sql.dataextractor;

import java.util.*;

import static org.sql.dataextractor.JoinExtractor.extractForeignKeys;
import static org.sql.patternsearch.RegexUtils.findMatch;
import static org.sql.patternsearch.RegexUtils.findMatches;

public class SQLExtractor {

    @SuppressWarnings("unchecked")
    public <T> Map<String, List<T>> extractDataToMap(String input, boolean isMappingJoin) {
        Map<String, List<T>> map = new HashMap<>();
        var inserts = findMatches(input, RegexExtractor.INSERT, 1);
        var dataCreate = findMatches(input, RegexExtractor.TABLE, 1);

        var foreignKey = extractForeignKeys(dataCreate);

        inserts.forEach(element -> {
            final String nameTable = findMatch(element, RegexExtractor.TABLE_NAME,1);
            final List<Map<String, T>> statements = new ArrayList<>();

            if(isMappingJoin && !foreignKey.get(nameTable).isEmpty()){
                statements.addAll(processStatement(element, inserts, foreignKey.get(nameTable)));
            }
            else statements.addAll(processStatement(element));

            map.put(nameTable, (List<T>) statements);
        });
        return map;
    }

    private <T> List<Map<String, T>> processStatement(String element){
        Map<String, List<String>> keyAndValue = extractKeyAndValue(element);
        return convertToMappedRecords(keyAndValue.get("values"), keyAndValue.get("columns"));
    }
    @SuppressWarnings("unchecked")
    private <T> T processStatement(String element, List<String> inserts,  List<Map<String, String>> foreignKeys){
        if(foreignKeys.size()==1){
            List<T> values = mergeWithForeignKeys(element, inserts, foreignKeys.getFirst());
            return (T)values;
        }

        List<T> listStatement = new ArrayList<>();
        foreignKeys.forEach(foreignKey -> {
            List<T> values = mergeWithForeignKeys(element, inserts, foreignKey);
            listStatement.add((T)values);
        });
        return (T)listStatement;
    }

    private <T> List<T> mergeWithForeignKeys(String element, List<String> inserts, Map<String, String> foreignKey) {
        String tableReference= foreignKey.get("table_reference");
        String reference = getReference(inserts, tableReference);

        final Map<String, List<String>> keyAndValueForeignKey = extractKeyAndValue(reference);
        final Map<String, List<String>> keyAndValue = extractKeyAndValue(element);

        return updateWithForeignKeys(keyAndValue, keyAndValueForeignKey, foreignKey);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> updateWithForeignKeys(Map<String, List<String>> keyAndValue, Map<String, List<String>> keyAndValueForeignKey, Map<String, String> foreignKey) {
        String tableReference=foreignKey.get("table_reference");
        String key = foreignKey.get("foreign_key");

        List<Map<String, T>> values = convertToMappedRecords(keyAndValue.get("values"), keyAndValue.get("columns"));
        final List<Map<String, T>> valuesForeignKey = convertToMappedRecords(keyAndValueForeignKey.get("values"), keyAndValueForeignKey.get("columns"));

        values.stream()
                .filter(value -> value.containsKey(key))
                .forEach(value ->{
                    value.remove(key);
                    int index = values.indexOf(value);
                    if(index < valuesForeignKey.size())
                        value.put(tableReference,(T)valuesForeignKey.get(index));
                });

        return (List<T>) values;
    }

    private static String getReference(List<String> inserts, String tableReference) {
        return inserts.stream().filter(value -> {
            String tableName = findMatch(value, RegexExtractor.TABLE_NAME,1);
            return tableReference.equals(tableName);
        }).findFirst().orElse(null);
    }






    private <T> List<Map<String, T>> convertToMappedRecords(List<String> listValues, List<String> columnsNames) {
        List<Map<String, T>> resultList = new ArrayList<>();

        listValues.forEach(values -> {
            List<String> columnValues = findMatches(values, RegexExtractor.INSERT_COLUMN_VALUES, 1);
            Map<String, T> valueMap = mapColumnValues(columnValues, columnsNames);
            resultList.add(valueMap);
        });

        return resultList;
    }

    @SuppressWarnings("unchecked")
    private <T> Map<String, T> mapColumnValues(List<String> columnValues, List<String> columnsNames) {
        Map<String, T> valueMap = new HashMap<>();

        for (int index = 0; index < columnValues.size(); index++) {
            String value = columnValues.get(index).replace("'", "");

            if (value.equals("NULL") || value.equals("FALSE") || value.equals("TRUE")) {
                value = value.toLowerCase();
            }
            valueMap.put(columnsNames.get(index), (T) value);
        }

        return valueMap;
    }
    private Map<String, List<String>> extractKeyAndValue(String element) {
        Map<String, List<String>> map = new HashMap<>();
        List<String> values = findMatches(element, RegexExtractor.INSERT_DATA, 1);
        final List<String> columns = findMatches(values.getFirst(), RegexExtractor.INSERT_COLUMN_NAME);

        values.removeFirst();
        map.put("columns", columns);
        map.put("values", values);
        return map;
    }
}