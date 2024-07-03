package com.github.pdaodao.springwebplus.starter.query;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

public class QueryBuilder {

    public static <T> LambdaQueryBuilder<T> lambda(Class<T> entityClass) {
        return new LambdaQueryBuilder(Wrappers.lambdaQuery(entityClass));
    }

}
