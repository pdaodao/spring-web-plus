package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class RichValue {
    // 字面值，函数名称, 变量名称
    private String value;

    private ValueType type;

    private List<RichValue> params;

    public boolean empty(){
        return CollUtil.isEmpty(params) || StrUtil.isBlank(value);
    }

    /**
     * 值类型
     */
    public static enum ValueType{
        // 函数
        fn,
        // 表达式
        ex,
        // 字面量
        lr,
        // 变量
        ls,
        // 变量
        vb
    }

    public Integer intValue(){
        if(StrUtil.isBlank(value)){
            return null;
        }
        return DataValueUtil.toInt(value);
    }

    public Long longValue(){
        if(StrUtil.isBlank(value)){
            return null;
        }
        return DataValueUtil.toLong(value);
    }

    public Double doubleValue() {
        if(StrUtil.isBlank(value)){
            return null;
        }
        return DataValueUtil.toDouble(value);
    }

    public Date dateValue(){
        if(StrUtil.isBlank(value)){
            return null;
        }
        return DataValueUtil.toDate(value);
    }
}
