package com.v5analytics.webster.utils;

public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean containsAnEmpty(String[] strings) {
        for (String str : strings) {
            if (StringUtils.isEmpty(str)) {
                return true;
            }
        }
        return false;
    }
}
