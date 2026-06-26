package com.github.pdaodao.springwebplus.ai.util;

import cn.hutool.core.util.StrUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;

public class AiTextUtil {

    /**
     * 在忽略简体繁体的情况下字符串是否相等
     * @param st1
     * @param st2
     * @return
     */
    public static boolean equals(final String st1, final String st2){
        return StrUtil.equals(toSimple(st1), toSimple(st2));
    }

    /**
     * 在忽略简体繁体的情况下字符串相似性
     * @param st1
     * @param st2
     * @return
     */
    public static double similar(final String st1, final String st2){
        return StrUtil.similar(toSimple(st1), toSimple(st2));
    }

    /**
     * 转为简体中文
     * @param st
     * @return
     */
    public static String toSimple(final String st){
        if(StrUtil.isBlank(st)){
            return st;
        }
        return ZhConverterUtil.toSimple(st);
    }

    /**
     * 转为繁体中文
     * @param st
     * @return
     */
    public static String toTraditional(final String st){
        if(StrUtil.isBlank(st)){
            return st;
        }
        return ZhConverterUtil.toTraditional(st);
    }
}
