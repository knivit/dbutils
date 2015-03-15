package com.tsoft.dbutils.dit;

import java.util.regex.*;

public class DITGenerateObject {
    public static int INCLUDE = 1;
    public static int EXCLUDE = 2;
    private String nameString;
    private String categoryString;    // ("trigger", "table" etc)
    private int operation;            // OBJECT_INCLUDE .. OBJECT_EXCLUDE
    private Pattern namePattern;
    private Pattern categoryPattern;

    public DITGenerateObject(String aNameString, String aCategoryString, int aOperation) {
        nameString = aNameString;
        categoryString = aCategoryString;
        operation = aOperation;

        namePattern = Pattern.compile(nameString, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
        categoryPattern = Pattern.compile(categoryString, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
    }

    public boolean isCategoryMatches(String input) {
        Matcher matcher = categoryPattern.matcher(input);
        return matcher.matches();
    }

    public boolean isNameMatches(String input) {
        Matcher matcher = namePattern.matcher(input);
        return matcher.matches();
    }

    public boolean isSkip() {
        return operation == EXCLUDE;
    }
}
