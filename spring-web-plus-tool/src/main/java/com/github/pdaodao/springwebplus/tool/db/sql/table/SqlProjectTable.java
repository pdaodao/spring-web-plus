package com.github.pdaodao.springwebplus.tool.db.sql.table;

import com.github.pdaodao.springwebplus.tool.db.sql.frame.SafeAppendable;
import lombok.Data;

@Data
public class SqlProjectTable extends SqlTable{
    public SqlTable mainTable;

    @Override
    public SqlProjectTable clone() {
        final SqlProjectTable mapTable = new SqlProjectTable();
        deepCopyTo(mapTable);
        if(mainTable != null){
            mapTable.mainTable = mainTable.clone();
        }
        return mapTable;
    }

    @Override
    public String toString() {
        return sql();
    }

    @Override
    public String selectSQL() {
        final SafeAppendable builder = new SafeAppendable();
        selectPart(builder, false);

        final String fromTableName = "(" + mainTable.sql()+") "+mainTable.tableAlias.alias();
        sqlClause(builder, "FROM", fromTableName, "", "", ", ");
        // where
        wherePart(builder, false);
        return builder.toString();
    }
}