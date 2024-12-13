package com.github.pdaodao.springwebplus.tool.db.core;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.Column;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * 字段信息
 */
@Data
public class TableColumn implements Serializable, Cloneable, Comparable<TableColumn> {
    /**
     * 小数点位数
     */
    public Integer scale;
    /**
     * 是否自增
     */
    public Boolean isAuto;
    /**
     * 字段名称(英文)
     */
    protected String name;

    /**
     * 来源字段
     */
    protected String from;

    /**
     * 中文名称
     */
    protected String title;
    /**
     * 备注
     */
    protected String remark;
    /**
     * 表名称
     */
    protected String tableName;
    /**
     * 字段类型名称
     */
    protected String typeName;
    /**
     * 字段长度
     */
    protected Integer length;

    /**
     * 排序
     */
    protected Integer seq;

    /**
     * 是否是主键
     */
    protected Boolean isPk;
    /**
     * 是否可为空
     */
    protected Boolean nullable;
    /**
     * 默认值
     */
    protected String defaultValue;
    /**
     * 标准字段类型
     */
    private DataType dataType;

    public static TableColumn of(final Column column) {
        final TableColumn r = new TableColumn();
        BeanUtil.copyProperties(column, r);
        return r;
    }

    public String getTitle() {
        if (StrUtil.isNotBlank(title)) {
            return title;
        }
        if (StrUtil.isNotBlank(remark)) {
            return StrUtils.clean(remark);
        }
        if (StrUtil.isNotBlank(name)) {
            return name;
        }
        return title;
    }

    /**
     * 是否相同
     *
     * @param info
     * @return
     */
    public boolean diff(final TableColumn info, boolean ignoreName) {
        // 字段名称不同
        if (!ignoreName && !StrUtil.equalsIgnoreCase(getName(), info.getName())) {
            return true;
        }
        // 默认值不同
        if (!ObjectUtil.equals(getDefaultValue(), info.getDefaultValue())) {
            return true;
        }
        // 标准字段类型不同
        if (ObjectUtil.notEqual(dataType, info.getDataType())) {
            if (dataType == DataType.STRING && info.getDataType() == DataType.TEXT) {
                // 字符串类型 和 text 类型比较
                return false;
            }
            if (dataType == DataType.DATETIME && info.getDataType() == DataType.TIMESTAMP) {
                return false;
            }
            if (dataType == DataType.BOOLEAN && info.getDataType() == DataType.INT) {
                return false;
            }
            if (dataType != info.dataType) {
                return true;
            }
            if (dataType.lengthNotRequired()) {
                return false;
            }
        }
        // 长度不同
        if (dataType != null && !dataType.lengthNotRequired()
                && dataType.isDoubleFamily()
                && !ObjectUtil.equals(getLength(), info.getLength())) {
            return true;
        }
        // 精度不同
        if (dataType.isDoubleFamily()) {
            if (getScale() != null && getScale() == 0) {
                setScale(null);
            }
            if (info.getScale() != null && info.getScale() == 0) {
                info.setScale(0);
            }
            if (info.getScale() != null && getScale() != null && info.getScale() > getScale()) {
                return false;
            }
            if (!ObjectUtil.equals(getScale(), info.getScale())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(TableColumn o) {
        if (true == this.isAuto) {
            return -1;
        }
        if (true == o.getIsAuto()) {
            return 1;
        }
        if (true == this.getIsPk()) {
            return -1;
        }
        if (true == o.getIsPk()) {
            return 1;
        }
        return CompareUtil.compare(seq, o.seq);
    }

    @Override
    protected TableColumn clone() {
        final TableColumn t = new TableColumn();
        BeanUtil.copyProperties(this, t);
        return t;
    }

    public void updateDataTypeIfNull(final DataType type) {
        if (dataType == null) {
            dataType = type;
        }
    }
}
