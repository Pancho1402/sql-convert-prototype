package org.sql.dataextractor;

import java.util.*;

import static org.sql.dataextractor.JoinExtractor.extractForeignKeys;
import static org.sql.dataextractor.MappingSql.getReference;
import static org.sql.dataextractor.MappingSql.processStatement;
import static org.sql.patternsearch.RegexUtils.findMatch;
import static org.sql.patternsearch.RegexUtils.findMatches;

public class SQLExtractor {
    private final boolean mapJoin;
    private final List<String> tableNames;
    private final List<String> data;
    private final List<String> dataCreate;

    public SQLExtractor(ExtractData data){
        this.mapJoin = data.mapJoin;
        this.tableNames = data.tableNames;

        this.data = findMatches(data.query, RegexExtractor.INSERT, 1);
        this.dataCreate = findMatches(data.query, RegexExtractor.TABLE, 1);
    }
    public Map<String, List<Map<String, Object>>> extractAndProcessData(){
        if(!tableNames.isEmpty()){
            return processByTableNames();
        }
        return processData();
    }
    private Map<String, List<Map<String, Object>>> processByTableNames(){
        Map<String, List<Map<String, Object>>> map = new HashMap<>();

        tableNames.forEach(value ->{
            String reference = getReference(data, value);
            List<Map<String, Object>> statements = buildStatements(reference, value);

            map.put(value, statements);
        });
        return map;
    }
    private Map<String, List<Map<String, Object>>> processData(){
        Map<String, List<Map<String, Object>>> map = new HashMap<>();

        data.forEach(element -> {
            String nameTable = findMatch(element, RegexExtractor.TABLE_NAME,1);
            List<Map<String, Object>> statements = buildStatements(element, nameTable);

            map.put(nameTable, statements);
        });
        return map;
    }

    private List<Map<String, Object>> buildStatements(String value, String nameTable) {
        if(mapJoin){
            final var foreignKeys = extractForeignKeys(dataCreate);
            if(!foreignKeys.get(nameTable).isEmpty())
                return processStatement(value, data, foreignKeys.get(nameTable));
        }
        return processStatement(value);
    }

    public static class ExtractData {
        private String query;
        private boolean mapJoin = false;
        private List<String> tableNames = new ArrayList<>();

        public SQLExtractor.ExtractData queryInput(String query){
            this.query = query;
            return this;
        }
        public SQLExtractor.ExtractData isMappingJoin(boolean mappingJoin){
            this.mapJoin = mappingJoin;
            return this;
        }
        public SQLExtractor.ExtractData byTableNames(List<String> tableNames){
            this.tableNames = tableNames;
            return this;
        }
        public SQLExtractor extract(){
            return new SQLExtractor(this);
        }
    }
}