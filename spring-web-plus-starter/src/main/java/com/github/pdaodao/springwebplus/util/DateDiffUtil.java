package com.github.pdaodao.springwebplus.util;

/**
 *
 **/
public class DateDiffUtil {

    /**
     * 计算相差的时间天到秒
     * xx天xx小时xx分钟xx秒
     *
     * @param diffTime
     * @return
     */
    public static String getDiffDaySecond(long diffTime) {
        // 计算相差的秒数
        long diff = diffTime / 1000;
        // 计算天
        long day = diff / (24 * 60 * 60);
        // 计算小时数
        long hour = diff / (60 * 60);
        // 计算分钟数
        long minute = (diff % (60 * 60)) / 60;
        // 计算秒数
        long second = diff % 60;
        final StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day).append("天");
        }
        if (hour > 0) {
            sb.append(hour).append("小时");
        }
        if (minute > 0) {
            sb.append(minute).append("分钟");
        }
        sb.append(second).append("秒");
        return sb.toString();
    }

}
