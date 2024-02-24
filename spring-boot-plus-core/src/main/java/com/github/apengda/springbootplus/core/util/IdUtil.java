package com.github.apengda.springbootplus.core.util;

public class IdUtil {
    public static final String BaseChar = "123456789abcdefghjkmnpqrstuvwxyz";

    private static String convertToAlphanumeric(long number) {
        int base = BaseChar.length();
        final StringBuilder result = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % base);
            result.insert(0, BaseChar.charAt(remainder));
            number /= base;
        }
        return result.toString();
    }

    public static String snowId() {
        final Long timeMillis = cn.hutool.core.util.IdUtil.getSnowflake().nextId();
        final String identifier = convertToAlphanumeric(timeMillis);
        return identifier;
    }
}
