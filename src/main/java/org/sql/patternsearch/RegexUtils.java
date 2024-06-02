package org.sql.patternsearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    private RegexUtils(){}

    public static String findMatch(String input, String regex, int groupIndex) {
        final Matcher matcher = getMatcher(input, regex);

        if (matcher!=null && matcher.find())
            return matcher.group(groupIndex);
        return null;
    }

    public static String findMatch(String input, String regex) {
        final Matcher matcher = getMatcher(input, regex);

        if (matcher!=null && matcher.find())
            return matcher.group();
        return null;
    }

    public static List<String> findMatches(String input, String regex,
                             int groupIndex) {
        final Matcher matcher = getMatcher(input, regex);

        if(matcher!=null){
            List<String> listMatcher = new ArrayList<>();
            while(matcher.find()) listMatcher.add(matcher.group(groupIndex));
            return listMatcher;
        }
        return Collections.emptyList();
    }

    public static List<String> findMatches(String input, String regex) {
        final Matcher matcher = getMatcher(input, regex);

        if(matcher!=null){
            List<String> listMatcher = new ArrayList<>();
            while(matcher.find()) listMatcher.add(matcher.group());
            return listMatcher;
        }
        return Collections.emptyList();
    }

    private static Matcher getMatcher(String input, String regex) {
        if(!input.isEmpty() && !regex.isEmpty())
            return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(input);
        return null;
    }
}