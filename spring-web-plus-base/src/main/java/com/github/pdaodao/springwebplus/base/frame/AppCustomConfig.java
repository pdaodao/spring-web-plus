package com.github.pdaodao.springwebplus.base.frame;

import java.util.Map;

/**
 * 应用个性化配置
 */
public interface AppCustomConfig {
    default String dateFormat() {
        return "yyyy-MM-dd";
    }

    default String datetimeFormat() {
        return "yyyy-MM-dd HH:mm:ss";
    }

    /**
     * 接口响应http状态码是否和数据状态码一致
     * @return
     */
    default boolean isUseResponseStatus(){
        return true;
    }

    default Map<Integer, Integer> responseCodeMap(){
        return null;
    }

}
