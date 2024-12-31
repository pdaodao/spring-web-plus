package com.github.pdaodao.springwebplus.base.query;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.pdaodao.springwebplus.tool.data.Tuple2;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LambdaQueryBuilder<T> {
    private LambdaQueryWrapper<T> wrap;

    public LambdaQueryBuilder(LambdaQueryWrapper<T> lambdaQueryWrapper) {
        this.wrap = lambdaQueryWrapper;
    }

    public LambdaQueryBuilder<T> selectExclude(final String... columnNames) {
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

    public OrLike startLike() {
        return new OrLike(this);
    }

    public OrList starOr() {
        return new OrList(this);
    }

    public LambdaQueryBuilder<T> like(String val, final SFunction<T, ?>... columnNames) {
        if (StringUtils.isEmpty(val) || columnNames == null || columnNames.length < 1) {
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

    public LambdaQueryBuilder<T> eq(SFunction<T, ?> columnName, Object val) {
        if (val == null || ObjectUtil.isEmpty(val)) {
            return this;
        }
        wrap.eq(columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> eq(boolean condition, SFunction<T, ?> columnName, Object val) {
        if (val == null || ObjectUtil.isEmpty(val) || false == condition) {
            return this;
        }
        wrap.eq(columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> neq(SFunction<T, ?> columnName, Object val) {
        if (val == null) {
            return this;
        }
        wrap.ne(columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> neq(boolean condition, SFunction<T, ?> columnName, Object val) {
        if (val == null) {
            return this;
        }
        wrap.ne(condition, columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> between(SFunction<T, ?> columnName, Object small, Object big) {
        if (small == null && big == null) {
            return this;
        }
        if (small != null && big != null) {
            wrap.between(columnName, small, big);
            return this;
        }
        if (small != null) {
            wrap.ge(columnName, small);
            return this;
        }
        wrap.lt(columnName, big);
        return this;
    }

    public LambdaQueryBuilder<T> isNull(SFunction<T, ?> columnName) {
        if (columnName == null) {
            return this;
        }
        wrap.isNull(columnName);
        return this;
    }

    public LambdaQueryBuilder<T> isNull(boolean condition, SFunction<T, ?> columnName) {
        if (!condition) {
            return this;
        }
        if (columnName == null) {
            return this;
        }
        wrap.isNull(columnName);
        return this;
    }

    public LambdaQueryBuilder<T> isNotNull(SFunction<T, ?> columnName) {
        if (columnName == null) {
            return this;
        }
        wrap.isNotNull(columnName);
        return this;
    }

    public LambdaQueryBuilder<T> ge(SFunction<T, ?> columnName, Object val) {
        if (val == null || ObjectUtil.isEmpty(val)) {
            return this;
        }
        wrap.ge(columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> gt(SFunction<T, ?> columnName, Object val) {
        if (val == null || ObjectUtil.isEmpty(val)) {
            return this;
        }
        wrap.gt(columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> lt(SFunction<T, ?> columnName, Object val) {
        if (val == null || ObjectUtil.isEmpty(val)) {
            return this;
        }
        wrap.lt(columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> le(SFunction<T, ?> columnName, Object val) {
        if (val == null) {
            return this;
        }
        wrap.le(columnName, val);
        return this;
    }

    public LambdaQueryBuilder<T> in(SFunction<T, ?> columnName, Collection<?> values) {
        if (CollUtil.isEmpty(values)) {
            return this;
        }
        wrap.in(columnName, values);
        return this;
    }

    public LambdaQueryBuilder<T> in(SFunction<T, ?> columnName, Object... values) {
        if (ArrayUtil.isEmpty(values)) {
            return this;
        }
        wrap.in(columnName, values);
        return this;
    }

    public LambdaQueryBuilder<T> notIn(SFunction<T, ?> columnName, Object... values) {
        if (values == null || values.length < 1) {
            return this;
        }
        wrap.notIn(columnName, values);
        return this;
    }

    public LambdaQueryBuilder<T> notIn(SFunction<T, ?> columnName, Collection<?> values) {
        if (values == null || values.size() < 1) {
            return this;
        }
        wrap.notIn(columnName, values);
        return this;
    }

    public LambdaQueryBuilder<T> last(String lastSql) {
        if (ObjectUtil.isEmpty(lastSql)) {
            return this;
        }
        wrap.last(lastSql);
        return this;
    }

    public LambdaQueryWrapper<T> getWrap() {
        return wrap;
    }

    public LambdaQueryWrapper<T> build() {
        return wrap;
    }

    public class OrList {
        private final LambdaQueryBuilder<T> builder;
        private List<Consumer<AbstractWrapper>> list = new ArrayList<>();

        public OrList(LambdaQueryBuilder<T> builder) {
            this.builder = builder;
        }

        public OrList like(final SFunction<T, ?> columnName, final String val) {
            if (columnName == null || StringUtils.isBlank(val)) {
                return this;
            }

            return this;
        }

        public OrList eq(final SFunction<T, ?> columnName, final Object val) {
            if (val == null) {
                return this;
            }
            list.add(wrapper -> wrapper.eq(columnName, val));
            return this;
        }

        public OrList in(SFunction<T, ?> columnName, Collection<?> vals) {
            if (columnName == null || CollUtil.isEmpty(vals)) {
                return this;
            }
            if (vals.size() == 1) {
                return eq(columnName, vals.iterator().next());
            }
            list.add(w -> w.in(columnName, vals));
            return this;
        }

        public LambdaQueryBuilder<T> end() {
            if (CollUtil.isEmpty(list)) {
                return builder;
            }
            wrap.and(tQueryWrapper -> {
                for (final Consumer fn : list) {
                    fn.accept(tQueryWrapper.or());
                }
            });
            return builder;
        }
    }

    public class OrLike {
        private final LambdaQueryBuilder<T> builder;
        private List<Tuple2<SFunction<T, ?>, String>> likes = new ArrayList<>();

        public OrLike(LambdaQueryBuilder<T> p) {
            builder = p;
        }

        public OrLike like(SFunction<T, ?> columnName, String val) {
            if (columnName == null || StringUtils.isBlank(val)) {
                return this;
            }
            likes.add(new Tuple2<>(columnName, val.trim()));
            return this;
        }

        public LambdaQueryBuilder<T> endLike() {
            if (CollUtil.isEmpty(likes)) {
                return builder;
            }
            if (likes.size() == 1) {
                wrap.like(likes.get(0).f0, likes.get(0).f1);
                return builder;
            }
            wrap.and(tQueryWrapper -> {
                for (final Tuple2<SFunction<T, ?>, String> column : likes) {
                    tQueryWrapper.or().like(column.f0, column.f1);
                }
            });
            return builder;
        }
    }

}