package com.github.apengda.springbootplus.core.util;

public class StrUtils {

    public static String cut(final String str, int maxLength) {
        if (str == null) {
            return str;
        }
        final String temp = str.trim();
        if (temp.length() <= maxLength) {
            return temp;
        }
        return temp.substring(0, maxLength);
    }
}
