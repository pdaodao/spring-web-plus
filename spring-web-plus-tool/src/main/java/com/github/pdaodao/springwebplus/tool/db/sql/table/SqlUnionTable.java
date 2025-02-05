package com.github.pdaodao.springwebplus.tool.db.sql.table;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.tool.db.sql.frame.SafeAppendable;

import java.util.ArrayList;
import java.util.List;

public class SqlUnionTable extends SqlTable{
    public List<SqlTable> tables = new ArrayList<>();

    public SqlUnionTable addTable(final SqlTable table){
        tables.add(table);
        return this;
    }

    @Override
    public String selectSQL() {
        final SafeAppendable builder = new SafeAppendable();
        String prefix = "";
        String suffix = "";
        if(CollUtil.isNotEmpty(select)){
            selectPart(builder, false);
            prefix = "(";
            suffix = ")";
        }
        final List<String> ts = new ArrayList<>();
        for(final SqlTable sqlTable: tables){
            ts.add(sqlTable.sql());
        }
        sqlClause(builder, "", ts, prefix, suffix, "\n UNION ALL \n");
        if(CollUtil.isEmpty(select)){
            return builder.toString();
        }
        builder.append(" "+tableAlias.alias());
        // where
        wherePart(builder, false);
        return builder.toString();
    }

    @Override
    public SqlUnionTable clone() {
        final SqlUnionTable uniTable = new SqlUnionTable();
        deepCopyTo(uniTable);
        final List<SqlTable> list = new ArrayList<>();
        for(final SqlTable t: tables){
            list.add(t.clone());
        }
        uniTable.tables = list;
        return uniTable;
    }
}
