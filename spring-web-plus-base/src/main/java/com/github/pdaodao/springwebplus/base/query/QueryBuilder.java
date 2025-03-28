package com.github.pdaodao.springwebplus.base.query;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.validation.constraints.NotNull;

public class QueryBuilder {

    public static <T> LambdaQueryBuilder<T> lambda(@NotNull Class<T> entityClass) {
        return new LambdaQueryBuilder(Wrappers.lambdaQuery(entityClass));
    }

    public static String likeValue(final String input){
        if(StrUtil.isBlank(input)){
            return null;
        }
        return "%"+StrUtil.trim(input)+"%";
    }
}
