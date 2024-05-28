package org.sql.dataextractor;

public class RegexExtractor {
    private RegexExtractor() {}

    public static final String INSERT = "INSERT\\sINTO\\s([^;]+);";
    public static final String INSERT_DATA = "\\((.+)+\\)";
    public static final String TABLE_NAME = "(?!KEY\\S|REFERENCES\\S)(\\w+)\\s?\\(";
    public static final String INSERT_COLUMN_NAME = "\\w+";
    public static final String INSERT_COLUMN_VALUES = "('([^']+)'|[\\w\\.]+)";
    public static final String TABLE = "CREATE\\sTABLE\\s+(\\w+\\s+\\(([^;])+\\);)";
    public static final String FOREIGN_KEY = "FOREIGN\\sKEY\\s\\((\\w+)\\)\\sREFERENCES\\s(\\w+)\\s?\\((\\w+)\\)";
}
