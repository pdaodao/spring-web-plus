package com.github.pdaodao.springwebplus.tool.sql.util.visitor;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.sql.util.SqlUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * sql语法树中的数据表
 */
@Data
public class SelectFromTable {
    private final SelectFromTables context;
    private String id;
    // 表名
    private String name;
    // 表别名
    private String alias;
    // 字段列表
    private List<SelectFromField> fields;
    // 关联表
    private List<SelectFromTable> joins;

    public SelectFromTable(SelectFromTables context) {
        this.context = context;
    }

    public static SelectFromTable of(final SelectFromTables context, final String name, final String alias){
        final SelectFromTable f = new SelectFromTable(context);
        f.setName(SqlUtil.dropSqlEscape(name));
        f.setAlias(alias);
        return f;
    }

    public void addJoin(final SelectFromTable join){
        if(join == null){
            return;
        }
        if(joins == null){
            joins = new ArrayList<>();
        }
        joins.add(join);
    }

    public void addField(final SelectFromField ff){
        if(ff == null){
            return;
        }
        if(fields == null){
            fields = new ArrayList<>();
        }
        fields.add(ff);
    }

    /**
     * 是否是数据表
     * @return
     */
    public boolean isTable(){
        return StrUtil.isNotBlank(name) && !name.trim().contains(" ");
    }

    @Override
    public String toString() {
        return "SelectFromTable{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", fields=" + fields +
                '}';
    }
}

