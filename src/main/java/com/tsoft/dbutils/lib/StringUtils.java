package com.tsoft.dbutils.lib;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StringUtils {
    public static String replaceAll(String str, String template, String value) {
        while (str.indexOf(template) != -1) {
            int pos = str.indexOf(template);
            int len = template.length();
            if ((pos + len) == str.length()) {
                str = str.substring(0, pos) + value;
            } else {
                str = str.substring(0, pos) + value + str.substring(pos + len, str.length());
            }
        }
        return str;
    }

    private static String replaceFirstAll(String s, char templateChar, String value) {
        int count;
        String str = "";
        for (count = 0; (count < s.length()) && (s.charAt(count) == templateChar); count++) {
            str += value;
        }
        if (count > 0) {
            s = str + s.substring(count);
        }
        return s;
    }

    public static String toHTML(String s) {
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        s = s.replaceAll("\"", "&quot;");

        int pos;
        s = replaceFirstAll(s, ' ', "&nbsp;");
        while ((pos = s.indexOf('\n')) != -1) {
            String ls = s.substring(0, pos);
            String rs = s.substring(pos + 1);
            s = ls + "<br>" + replaceFirstAll(rs, ' ', "&nbsp;");
        }
        return s;
    }

    public static String toXML(String s) {
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        s = s.replaceAll("\"", "&quot;");
        return s;
    }

    public static boolean strContains(String s, String[] strList) {
        s = s.toUpperCase();
        for (int i = 0; i < strList.length; i++) {
            if (s.length() == strList[i].length() && (s.equals(strList[i].toUpperCase()))) {
                return true;
            }
        }
        return false;
    }

    public static String getNow() {
        return DateFormat.getDateTimeInstance().format(new Date());
    }

    public static void createList(String commaDelimitedString, ArrayList<String> result) {
        result.clear();
        while (commaDelimitedString.length() > 0) {
            int pos = commaDelimitedString.indexOf(',');
            if (pos == -1) {
                result.add(commaDelimitedString);
                return;
            }
            result.add(commaDelimitedString.substring(0, pos));
            commaDelimitedString = commaDelimitedString.substring(pos + 1).trim();
        }
    }
}
