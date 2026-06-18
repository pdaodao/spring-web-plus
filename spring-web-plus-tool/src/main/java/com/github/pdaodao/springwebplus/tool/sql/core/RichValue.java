package com.github.pdaodao.springwebplus.tool.sql.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RichValue {
    // 字面值，函数名称, 变量名称
    private Object value;

    private ValueType type;

    private List<RichValue> values;

    public boolean empty(){
        return CollUtil.isEmpty(values) && ObjectUtil.isEmpty(value);
    }

    public Object get(final int index){
        if(values == null){
            return null;
        }
        return values.get(index).getValue();
    }

    public int size(){
        int s = CollUtil.size(values);
        if(s > 0){
            return 0;
        }
        return ObjectUtil.isNotNull(value) ? 1 : 0;
    }

    public RichValue addToValues(final ValueType type, Object value){
        if(values == null){
            values = new ArrayList<>();
        }
        final RichValue rich = new RichValue();
        rich.setType(type);
        rich.setValue(value);
        values.add(rich);
        return this;
    }


    /**
     * 字面量参数值
     * @param value
     * @return
     */
    public static RichValue ofLiteral(final Object... value){
        final RichValue rich = new RichValue();
        rich.setType(ValueType.lr);
        if(CollUtil.size(value) == 1){
            rich.setValue(value);
        }
        if(CollUtil.size(value) > 1){
            final List<RichValue> list = new ArrayList<>();
            for(final Object v: value){
                list.add(RichValue.ofLiteral(v));
            }
            rich.setValues(list);
        }
        return rich;
    }

    @Override
    public String toString() {
        if(CollUtil.isEmpty(values)){
            return StrUtil.toStringOrEmpty(value);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(StrUtil.toStringOrEmpty(value));
        sb.append("(");
        sb.append(values.stream().map(t -> t.toString()).collect(Collectors.joining(",")));
        sb.append(")");
        return sb.toString();
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
        vb
    }

    public Integer intValue(){
        if(ObjectUtil.isEmpty(value)){
            return null;
        }
        return DataValueUtil.toInt(value);
    }

    public Long longValue(){
        if(ObjectUtil.isEmpty(value)){
            return null;
        }
        return DataValueUtil.toLong(value);
    }

    public Double doubleValue() {
        if(ObjectUtil.isEmpty(value)){
            return null;
        }
        return DataValueUtil.toDouble(value);
    }

    public LocalDateTime dateValue(){
        if(ObjectUtil.isEmpty(value)){
            return null;
        }
        return DataValueUtil.toLocalDate(value);
    }
}
