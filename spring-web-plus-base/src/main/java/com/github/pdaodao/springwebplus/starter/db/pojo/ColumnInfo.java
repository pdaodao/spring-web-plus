package com.github.pdaodao.springwebplus.starter.db.pojo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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

    /**
     * 是否相同
     * @param info
     * @return
     */
    public boolean diff(final ColumnInfo info, boolean ignoreName) {
        // 字段名称不同
        if(!ignoreName && !StrUtil.equalsIgnoreCase(getName(), info.getName())){
            return true;
        }
        // 默认值不同
        if(!ObjectUtil.equals(getColumnDef(), info.getColumnDef())){
            return true;
        }
        // 标准字段类型不同
        if(dataType != null && info.dataType != null){
            if(dataType == DataType.STRING && info.getDataType() == DataType.TEXT){
                // 字符串类型 和 text 类型比较
                return false;
            }
            if(dataType != info.dataType){
                return true;
            }
            if(dataType.lengthNotRequired()){
                return false;
            }
        }
        // 长度不同
        if(!ObjectUtil.equals(getSize(), info.getSize())){
            return true;
        }
        // 精度不同
        if(!ObjectUtil.equals(getDigit(), info.getDigit())){
            return true;
        }
        return false;
    }
}
