package com.github.pdaodao.springwebplus.tool.db.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.sql.NamedSql;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 过滤参数为 map 结构的 sql
 */
@Data
public class SqlWithMapParams {
    private String dbId;
    private String sql;
    private Map<String, Object> params;


    public static SqlWithMapParams of(final String sql, final Map<String, Object> params) {
        final SqlWithMapParams r = new SqlWithMapParams();
        r.setSql(sql);
        r.setParams(params);
        return r;
    }

    public NamedSql toNamedSql() {
        final NamedSql namedSql = new NamedSql(sql, params);
        return namedSql;
    }

    /**
     * 添加过滤参数到 map 中,返回参数名称
     *
     * @param name
     * @param value
     * @return
     */
    public String addParam(String name, final Object value) {
        if (StrUtil.isBlank(name) || value == null) {
            return null;
        }
        if (params == null) {
            params = new LinkedHashMap<>();
        }
        name = name.trim();
        if (params.containsKey(name)) {
            for (int i = 0; i < 10000; i++) {
                if (!params.containsKey(name + i)) {
                    name = name + i;
                    break;
                }
            }
        }
        params.put(name, value);
        return name;
    }
}
