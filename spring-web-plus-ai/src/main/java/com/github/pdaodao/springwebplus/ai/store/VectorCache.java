package com.github.pdaodao.springwebplus.ai.store;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import java.time.Duration;

/**
 * 向量缓存
 */
@Data
public class VectorCache {
    private Cache<String, float[]> cacheMap;

    public VectorCache() {
         cacheMap = Caffeine.newBuilder()
                .maximumSize(20000)
                .expireAfterAccess(Duration.ofMinutes(20))
                .build();
    }

    public void put(final String key, float[] embed){
        if(StrUtil.isBlank(key) || ArrayUtil.isEmpty(embed)){
            return;
        }
        cacheMap.put(StrUtil.trim(key), embed);
    }

    public float[] get(final String key){
        if(StrUtil.isBlank(key)){
            return null;
        }
        return cacheMap.getIfPresent(StrUtil.trim(key));
    }

    public boolean contains(final String key){
        if(StrUtil.isBlank(key)){
            return true;
        }
        final float[] ret = cacheMap.getIfPresent(StrUtil.trim(key));
        return ArrayUtil.isNotEmpty(ret);
    }
}
