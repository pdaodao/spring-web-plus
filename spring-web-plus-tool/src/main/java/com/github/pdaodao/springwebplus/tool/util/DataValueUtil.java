package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.util.ObjectUtil;

public class DataValueUtil {

    public static String convertToString(final Object obj) {
        if (obj == null) {
            return null;
        }
        return ObjectUtil.toString(obj);
    }
}
