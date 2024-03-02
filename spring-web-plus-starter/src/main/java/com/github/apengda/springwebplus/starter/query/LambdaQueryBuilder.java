package com.github.apengda.springwebplus.starter.query;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class LambdaQueryBuilder<T> {
    private final LambdaQueryWrapper<T> wrap;

    public LambdaQueryBuilder(LambdaQueryWrapper<T> wrap) {
        this.wrap = wrap;
    }

    public LambdaQueryBuilder<T> eq(SFunction<T, ?> columnName, Object val) {
        wrap.eq(ObjectUtil.isNotNull(val), columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> like(String val, final SFunction<T, ?>... columnNames) {
        if (StrUtil.isBlank(val) || ArrayUtil.isEmpty(columnNames)) {
            return this;
        }
        val = val.trim();
        if (columnNames.length == 1) {
            wrap.like(columnNames[0], val);
            return this;
        }
        String finalVal = val;
        wrap.and(tQueryWrapper -> {
            for (final SFunction column : columnNames) {
                tQueryWrapper.or().like(column, finalVal);
            }
        });
        return this;
    }


    public LambdaQueryBuilder<T> neq(SFunction<T, ?> columnName, Object val) {
        wrap.ne(ObjectUtil.isNotNull(val), columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> isNull(SFunction<T, ?> columnName) {
        wrap.isNull(columnName);
        return this;
    }

    public LambdaQueryBuilder<T> isNull(boolean condition, SFunction<T, ?> columnName) {
        wrap.isNull(condition, columnName);
        return this;
    }

    public LambdaQueryBuilder<T> gt(SFunction<T, ?> columnName, Object val) {
        wrap.gt(ObjectUtil.isNotNull(val), columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> ge(SFunction<T, ?> columnName, Object val) {
        wrap.ge(ObjectUtil.isNotNull(val), columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> lt(SFunction<T, ?> columnName, Object val) {
        wrap.lt(ObjectUtil.isNotNull(val), columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> le(SFunction<T, ?> columnName, Object val) {
        wrap.le(ObjectUtil.isNotNull(val), columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> in(SFunction<T, ?> columnName, Collection<?> values) {
        wrap.in(CollUtil.isNotEmpty(values), columnName, values);
        return this;
    }

    public LambdaQueryBuilder<T> notIn(SFunction<T, ?> columnName, Collection<?> values) {
        wrap.notIn(CollUtil.isNotEmpty(values), columnName, values);
        return this;
    }

    public LambdaQueryBuilder<T> between(SFunction<T, ?> columnName, Object small, Object big) {
        if (ObjectUtil.isNull(small) && ObjectUtil.isNull(big)) {
            return this;
        }
        if (ObjectUtil.isNotNull(small) && ObjectUtil.isNotNull(big)) {
            wrap.between(columnName, small, big);
            return this;
        }
        if (ObjectUtil.isNotNull(small)) {
            wrap.ge(columnName, small);
            return this;
        }
        wrap.lt(columnName, big);
        return this;
    }

    public LambdaQueryBuilder<T> selectExcludes(final String... columnNames) {
        if (columnNames == null) {
            return this;
        }
        final Set<String> names = Arrays.stream(columnNames).collect(Collectors.toSet());
        this.wrap.select(tableFieldInfo -> {
            if (names.contains(tableFieldInfo.getColumn())) {
                return false;
            }
            return true;
        });
        return this;
    }

    public LambdaQueryWrapper<T> build() {
        return wrap;
    }
}
