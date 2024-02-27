package com.github.apengda.springwebplus.starter.db.pojo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.db.meta.Column;
import lombok.Data;


@Data
public class ColumnInfo extends Column {
    private DataType dataType;

    public static ColumnInfo of(final Column column) {
        final ColumnInfo r = new ColumnInfo();
        BeanUtil.copyProperties(column, r);
        return r;
    }
}
