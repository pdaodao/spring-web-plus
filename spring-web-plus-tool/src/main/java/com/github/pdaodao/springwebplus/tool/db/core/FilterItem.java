package com.github.pdaodao.springwebplus.tool.db.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 过滤条件项
 */
@Data
public class FilterItem {
    /**
     * 字段
     */
    private String name;

    /**
     * 字段中文名称
     */
    private String title;

    /**
     * 标准字段类型
     */
    private DataType dataType;

    /**
     * 比较符号
     */
    private WhereOperator op;

    /**
     * 参数值
     */
    private List<Object> params;

    /**
     * 字典id
     */
    private String dicId;


    public static FilterItem of(final String name, final WhereOperator op, Object... params) {
        final FilterItem f = new FilterItem();
        f.setName(name);
        f.setOp(op);
        f.setParams(Convert.toList(Object.class, params));
        return f;
    }

    /**
     * 多个过滤条件项目 转为 字段：参数 map
     *
     * @param filterItems
     * @return
     */
    public static Map<String, Object> toParamMap(final List<FilterItem> filterItems) {
        final Map<String, Object> params = new LinkedHashMap<>();
        if (CollUtil.isEmpty(filterItems)) {
            return params;
        }
        for (final FilterItem f : filterItems) {
            if (f.paramSize() < 1) {
                params.put(f.getName(), null);
            } else if (f.paramSize() > 1) {
                params.put(f.getName(), f.getParams());
            } else {
                params.put(f.getName(), f.getParam());
            }
        }
        return params;
    }

    @JsonIgnore
    public Object getParam() {
        if (CollUtil.isEmpty(params)) {
            return null;
        }
        return params.get(0);
    }

    @JsonIgnore
    public void setParam(final Object p) {
        if (ObjectUtil.isNull(p)) {
            return;
        }
        if (params == null) {
            params = new ArrayList<>();
        }
        params.add(p);
    }

    /**
     * 参数个数
     *
     * @return
     */
    public int paramSize() {
        if (CollUtil.isEmpty(params)) {
            return 0;
        }
        return params.size();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ");
        if (op != null) {
            sb.append(op.sql);
        } else {
            sb.append("?");
        }
        sb.append(" ");
        if (CollUtil.isNotEmpty(params)) {
            sb.append(StrUtil.join(",", params));
        }
        return sb.toString();
    }
}
