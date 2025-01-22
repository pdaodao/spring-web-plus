package com.github.pdaodao.springwebplus.tool.db.sql.table;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.github.pdaodao.springwebplus.tool.db.pojo.JoinType;
import com.github.pdaodao.springwebplus.tool.db.sql.frame.SafeAppendable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SqlJoinTable extends SqlTable{
    public transient SqlTable mainTable;
    public transient List<JoinItem> joins = new ArrayList<>();

    public SqlJoinTable join(final JoinType joinType, final SqlTable sqlStatement, String... ons){
        final JoinItem join = new JoinItem(joinType, sqlStatement, ListUtil.of(ons));
        joins.add(join);
        return this;
    }

    @Override
    public String selectSQL() {
        if(CollUtil.isEmpty(joins)){
            return super.selectSQL();
        }
        final SafeAppendable builder = new SafeAppendable();
        // select
        if(CollUtil.isNotEmpty(select)){
            selectPart(builder, false);
            builder.append("\nFROM (");
        }

        // 主表是否需要作为子查询
        final String fromTableName = mainTable.isAsSubQuery() ?  mainTable.subQuerySql() : mainTable.tableNameWithAlias(true);
        final List<String> fs = new ArrayList<>();
        fs.addAll(mainTable.selectFieldsWithTableAlias( true));
        for(final JoinItem joinItem: joins){
            fs.addAll(joinItem.table.selectFieldsWithTableAlias(true));
        }
        selectPart(builder, fs, true);
        sqlClause(builder, "FROM", fromTableName, "", "", ", ");
        joinSqlBlock(builder);
        joinWhereBlock(builder);

        if(CollUtil.isNotEmpty(select)){
            builder.append("\n)").append(" ").append(tableAlias.alias());
        }
        // where
        wherePart(builder, false);
        return builder.toString();
    }

    private void joinWhereBlock(SafeAppendable builder){
        final List<String> ws = new ArrayList<>();
        if(!mainTable.isAsSubQuery() && CollUtil.isNotEmpty(mainTable.where)){
            ws.addAll(mainTable.processFieldWithTableAlias(mainTable.where, true));
        }
        for(final JoinItem join: joins){
            if(join.table.isAsSubQuery()){
                continue;
            }
            ws.addAll(join.table.processFieldWithTableAlias(join.table.where, true));
        }
        sqlClause(builder, "WHERE", ws, "(", ")", " AND ");
    }


    /**
     * 关联语句块
     * @param builder
     * @return
     */
    private void joinSqlBlock(SafeAppendable builder) {
        if(CollUtil.isEmpty(joins)){
            return;
        }
        final Map<String, String> tableAliasMap = new HashMap<>();
        tableAliasMap.put(mainTable.getTableName(), mainTable.tableAlias.alias());

        for(final JoinItem join: joins){
            String joinTable = join.table.tableNameWithAlias(true);
            // 作为子查询
            if(join.table.isAsSubQuery()){
                joinTable = "\n"+join.table.subQuerySql();
            }
            tableAliasMap.put(join.table.getTableName(), join.table.tableAlias.alias());
            sqlClause(builder, join.joinType.name()+" JOIN", joinTable, "", "", "");
            if(CollUtil.isNotEmpty(join.onExpressions)){
                final List<String> ons = new ArrayList<>();
                for(String on: join.onExpressions){
                    on = on.replaceAll("\\{\\}", mainTable.tableAlias.alias());
                    on = on.replaceAll("\\{r\\}", join.table.tableAlias.alias());
                    for(Map.Entry<String, String> entry: tableAliasMap.entrySet()){
                        on = on.replaceAll("\\{"+entry.getKey()+"\\}", entry.getValue());
                    }
                    ons.add(on);
                }
                sqlClause(builder, "ON",   ons, "", "", " AND ");
            }
        }
    }

    @Override
    public SqlJoinTable clone() {
        final SqlJoinTable join = new SqlJoinTable();
        deepCopyTo(join);
        if(mainTable != null){
            join.mainTable = mainTable.clone();
        }
        final List<JoinItem> js = new ArrayList<>();
        for(final JoinItem ji: joins){
            js.add(ji.clone());
        }
        join.joins = js;
        return join;
    }

    @Override
    public String toString() {
        return sql();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinItem implements Cloneable{
        public JoinType joinType;
        public SqlTable table;
        public List<String> onExpressions;

        @Override
        protected JoinItem clone(){
            final JoinItem item = new JoinItem();
            item.setJoinType(joinType);
            if(table != null){
                item.table = table.clone();
            }
            if(CollUtil.isNotEmpty(onExpressions)){
                item.onExpressions = ListUtil.list(false, onExpressions);
            }
            return item;
        }
    }
}
