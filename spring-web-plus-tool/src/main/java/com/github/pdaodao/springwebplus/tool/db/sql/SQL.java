package com.github.pdaodao.springwebplus.tool.db.sql;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.pojo.JoinType;
import com.github.pdaodao.springwebplus.tool.db.pojo.NameAlias;
import com.github.pdaodao.springwebplus.tool.db.pojo.StatementType;
import com.github.pdaodao.springwebplus.tool.db.sql.frame.SafeAppendable;
import com.github.pdaodao.springwebplus.tool.db.sql.table.SqlJoinTable;
import com.github.pdaodao.springwebplus.tool.db.sql.table.SqlProjectTable;
import com.github.pdaodao.springwebplus.tool.db.sql.table.SqlTable;
import com.github.pdaodao.springwebplus.tool.db.sql.table.SqlUnionTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

public class SQL {
    public TableAliasCounter aliasHelper;
    public SqlTable table;
    public TableAlias tableAlias;

    private SQL(TableAliasCounter holder) {
        this.aliasHelper = holder;
        this.tableAlias = TableAlias.of(holder);
        this.table = new SqlTable();
        this.table.setTableAlias(tableAlias);
    }

    private SQL(TableAliasCounter holder, SqlTable table) {
        this.aliasHelper = holder;
        this.table = table;
        this.tableAlias = table.tableAlias;
    }

    public static SQL of(TableAliasCounter holder){
        return new SQL(holder);
    }

    public static SQL of(){
        return new SQL(new TableAliasCounter());
    }

    public SQL update(String table) {
        sqlStatement().statementType = StatementType.UPDATE;
        sqlStatement().table(table);
        return this;
    }

    public SQL set(String sets) {
        sqlStatement().sets.add(sets);
        return this;
    }


    public SQL set(String... sets) {
        sqlStatement().sets.addAll(Arrays.asList(sets));
        return this;
    }

    public SQL insertInto(String tableName) {
        sqlStatement().statementType = StatementType.INSERT;
        sqlStatement().table(tableName);
        return this;
    }

    public SQL values(String columns, String values) {
        intoColumns(StrUtil.splitToArray(columns, ','));
        intoValues(StrUtil.splitToArray(values, ','));
        return this;
    }

    public SQL intoColumns(String... columns) {
        sqlStatement().insertColumns.addAll(Arrays.asList(columns));
        return this;
    }

    public SQL intoValues(String... values) {
        Collections.addAll(sqlStatement().valuesList, ListUtil.list(true, values));
        return this;
    }

    public SQL select(final String... columns) {
        sqlStatement().statementType = StatementType.SELECT;
        if(ArrayUtil.isEmpty(columns)){
            return this;
        }
        final Pattern asP = Pattern.compile("\\sAS\\s", Pattern.CASE_INSENSITIVE);
        if(columns.length == 1 && columns[0].contains(",") && !columns[0].contains("(")){
            for(String item: columns[0].split(",")){
                item = item.trim();
                String[] sp = asP.split(item);
                if(sp.length == 2){
                    sqlStatement().select.add(NameAlias.of(sp[0], sp[1]));
                    continue;
                }
                sqlStatement().select.add(NameAlias.of(item, null));
            }
            return this;
        }
        for(final String f : columns){
            String[] sp =   asP.split(f);
            if(sp.length == 2){
                sqlStatement().select.add(NameAlias.of(sp[0], sp[1]));
                continue;
            }
            sqlStatement().select.add(NameAlias.of(f, null));
        }
        return this;
    }

    public SQL select(NameAlias... columns) {
        sqlStatement().statementType = StatementType.SELECT;
        sqlStatement().select.addAll(Arrays.asList(columns));
        return this;
    }

    public SQL selectDistinct(String... columns) {
        sqlStatement().distinct = true;
        select(columns);
        return this;
    }

    public SQL selectDistinct() {
        sqlStatement().distinct = true;
        return this;
    }

    public SQL deleteFrom(String table) {
        sqlStatement().statementType = StatementType.DELETE;
        sqlStatement().table(table);
        return this;
    }

    public SQL from(String table) {
        sqlStatement().table(table);
        return this;
    }

    // project , map
    public SQL project(){
        final SqlProjectTable mapTable = new SqlProjectTable();
        mapTable.setTableAlias(TableAlias.of(aliasHelper));
        mapTable.setStatementType(StatementType.SELECT);
        mapTable.mainTable = sqlStatement();
        final SQL sql = new SQL(aliasHelper,  mapTable);
        return sql;
    }

    public SQL join(final JoinType joinType, final SQL join, String... ons) {
        if(sqlStatement() instanceof SqlJoinTable){
            ((SqlJoinTable) sqlStatement()).join(joinType, join.sqlStatement(), ons);
            return this;
        }
        final SqlJoinTable joinTable = new SqlJoinTable();
        joinTable.setTableAlias(TableAlias.of(aliasHelper));
        joinTable.setStatementType(StatementType.SELECT);
        joinTable.mainTable = sqlStatement();
        joinTable.join(joinType, join.sqlStatement(), ons);
        final SQL sql = new SQL(aliasHelper,  joinTable);
        return sql;
    }

    public SQL union(final SQL... unions) {
        if(sqlStatement() instanceof SqlUnionTable){
            for(final SQL u: unions){
                ((SqlUnionTable)sqlStatement()).addTable(u.sqlStatement());
            }
            return this;
        }
        final SqlUnionTable unionTable = new SqlUnionTable();
        unionTable.setTableAlias(tableAlias);
        unionTable.setStatementType(StatementType.SELECT);
        unionTable.addTable(table);
        for(final SQL u : unions){
            unionTable.addTable(u.sqlStatement());
        }
        return new SQL(aliasHelper, unionTable);
    }

    public SQL where(String conditions) {
        sqlStatement().where.add(conditions);
        sqlStatement().lastList = sqlStatement().where;
        return this;
    }

    public SQL where(String... conditions) {
        sqlStatement().where.addAll(Arrays.asList(conditions));
        sqlStatement().lastList = sqlStatement().where;
        return this;
    }

    public SQL or() {
        sqlStatement().lastList.add(SafeAppendable.OR);
        return this;
    }

    public SQL and() {
        sqlStatement().lastList.add(SafeAppendable.AND);
        return this;
    }

    public SQL groupBy(String... columns) {
        sqlStatement().groupBy.addAll(Arrays.asList(columns));
        return this;
    }

    public SQL having(String... conditions) {
        sqlStatement().having.addAll(Arrays.asList(conditions));
        sqlStatement().lastList = sqlStatement().having;
        return this;
    }

    public SQL orderBy(String columns) {
        sqlStatement().orderBy.add(columns);
        return this;
    }

    public SQL orderBy(String... columns) {
        sqlStatement().orderBy.addAll(Arrays.asList(columns));
        return this;
    }

    public SQL size(long size) {
        sqlStatement().setSize(size);
        return this;
    }

    public SQL offset(long value) {
        sqlStatement().setOffset(value);
        return this;
    }

    public SQL addRow() {
        sqlStatement().valuesList.add(new ArrayList<>());
        return this;
    }

    public SqlTable sqlStatement() {
        return table;
    }

    public SQL clone(){
        final SQL sql2 =  new SQL(aliasHelper, table.clone());
        sql2.aliasHelper = aliasHelper;
        return sql2;
    }

    @Override
    public String toString() {
        return sqlStatement().toString();
    }
}
