package org.sql.dataextractor;

import org.sql.patternsearch.RegexUtils;

import java.util.*;

import static org.sql.patternsearch.RegexUtils.findMatch;
import static org.sql.patternsearch.RegexUtils.findMatches;

public class SQLInsertExtractor {

    @SuppressWarnings("unchecked")
    public <T> Map<String, List<T>> extractDataToMap(String input, boolean isMappingJoin) {
        Map<String, List<T>> map = new HashMap<>();
        var inserts = findMatches(input, RegexExtractor.INSERT, 1);
        var dataCreate = findMatches(input, RegexExtractor.TABLE, 1);

        Map<String, Map<String, String>> foreignKey = extractForeignKeys(dataCreate);

        inserts.forEach(element -> {
            final String nameTable = findMatch(element, RegexExtractor.TABLE_NAME,1);
            final List<Map<String, T>> statements = new ArrayList<>();

            if(isMappingJoin && foreignKey.get(nameTable).get("column_reference")!=null){
                statements.addAll(processStatement(element, inserts, foreignKey.get(nameTable)));
            }
            else statements.addAll(processStatement(element));

            map.put(nameTable, (List<T>) statements);
        });
        return map;
    }
    @SuppressWarnings("unchecked")
    public <T> T processStatement(String element, List<String> inserts,  Map<String, String> foreignKey){
        int indexReference = 0;
        String tableReference=foreignKey.get("table_reference");
        for(int i = 0; i < inserts.size(); i++){
            String tableName = findMatch(inserts.get(i), RegexExtractor.TABLE_NAME,1);
            if(tableReference.equals(tableName)){
                indexReference=i;
                break;
            }
        }
        Map<String, List<String>> keyAndValueForeignKey = extractKeyAndValue(inserts.get(indexReference));
        Map<String, List<String>> keyAndValue = extractKeyAndValue(element);

        List<Map<String, T>> values = convertToMappedRecords(keyAndValue.get("values"), keyAndValue.get("columns"));
        List<Map<String, T>> valuesForeignKey = convertToMappedRecords(keyAndValueForeignKey.get("values"), keyAndValueForeignKey.get("columns"));

        for(int i =0; i< values.size(); i++){
            String key = foreignKey.get("foreign_key");
            if(values.get(i).containsKey(key)){
                values.get(i).remove(key);
                values.get(i).put(tableReference,(T)valuesForeignKey.get(i));
            }
        }
        return (T)values;
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

    private <T> List<Map<String, T>> processStatement(String element){
        Map<String, List<String>> keyAndValue = extractKeyAndValue(element);
        return convertToMappedRecords(keyAndValue.get("values"), keyAndValue.get("columns"));
    }

    public Map<String, Map<String, String>> extractForeignKeys(List<String> dataCreate){
        Map<String, Map<String, String>> foreignKey = new HashMap<>();

        dataCreate.forEach(element -> {
            if(!getForeignKey(element).isEmpty()){
                foreignKey.putAll(getForeignKey(element));
            }
        });
        return foreignKey;
    }
    public Map<String, Map<String, String>> getForeignKey(String element){
        final String tableName = RegexUtils.findMatch(element, RegexExtractor.TABLE_NAME,1);
        Map<String, Map<String, String>> map = new HashMap<>();
        Map<String, String> foreignKey = new HashMap<>();

        foreignKey.put("foreign_key", valuesInForeignKey(element, 1));
        foreignKey.put("table_reference", valuesInForeignKey(element, 2));
        foreignKey.put("column_reference", valuesInForeignKey(element, 3));

        map.put(tableName, foreignKey);
        return map;
    }
    public String valuesInForeignKey(String element, int groupIndex){
        return RegexUtils.findMatch(element, RegexExtractor.FOREIGN_KEY, groupIndex);
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