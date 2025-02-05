package com.github.pdaodao.springwebplus.tool.db.sql.table;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.github.pdaodao.springwebplus.tool.db.pojo.NameAlias;
import com.github.pdaodao.springwebplus.tool.db.pojo.StatementType;
import com.github.pdaodao.springwebplus.tool.db.sql.frame.SafeAppendable;
import com.github.pdaodao.springwebplus.tool.db.sql.TableAlias;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SqlTable implements Cloneable{
    String tableName;
    public transient TableAlias tableAlias;
    public StatementType statementType;

    // select 查询
    // select字段
    public List<NameAlias> select = new ArrayList<>();
    public List<String> where = new ArrayList<>();
    public List<String> having = new ArrayList<>();
    public List<String> groupBy = new ArrayList<>();
    public List<String> orderBy = new ArrayList<>();
    public boolean distinct;

    Long offset;
    Long size;


    // update set  a = a
    public List<String> sets = new ArrayList<>();

    public List<String> lastList = new ArrayList<>();

    // insert 字段
    public List<String> insertColumns = new ArrayList<>();

    public List<List<String>> valuesList = new ArrayList<>();

    public SqlTable table(final String table){
        this.tableName = table;
        return this;
    }

    public String sql() {
        Preconditions.checkNotNull(statementType, "sql type is null.");
        switch (statementType) {
            case DELETE: return deleteSQL();
            case INSERT: return insertSQL();
            case SELECT: return selectSQL();
            case UPDATE: return updateSQL();
        }
        return null;
    }

    /**
     * 处理字段带上表的别名
     * @param fs
     * @return
     */
    protected String processFieldWithTableAlias(final String fs, final boolean isTableAlias){
        if(StrUtil.isBlank(fs)){
            return fs;
        }
        if(isTableAlias){
            return fs.replaceAll("\\{\\}", tableAlias.alias());
        }
        return fs.replaceAll("\\{\\}\\.", "");
    }

    /**
     * 处理字段是否需要带表的别名
     * @param fs
     * @param isTableAlias
     * @return
     */
    protected List<String> processFieldWithTableAlias(final List<String> fs, final boolean isTableAlias){
        if(CollUtil.isEmpty(fs)){
            return fs;
        }
        final List<String> ret = new ArrayList<>();
        for(String f: fs){
            if(isTableAlias){
                f = TableAlias.addTableAlias(f, tableAlias.alias());
            }else{
                f = f.replaceAll("\\{\\}\\.", "");
            }
            ret.add(f);
        }
        return ret;
    }

    protected List<String> selectFieldsWithTableAlias(final boolean withTableAlis){
        final List<String> fs = new ArrayList<>();
        final boolean isSub = isAsSubQuery();
        for(final NameAlias nameAlias: select){
            if(withTableAlis && isSub){
                fs.add(tableAlias.alias()+"."+nameAlias.alias());
            }else{
                fs.add(nameAlias.toString(withTableAlis ? tableAlias.alias() : null));
            }
        }
        return fs;
    }

    /**
     * where ... 部分sql
     * @param sb
     * @param withTableAlis
     */
    protected void wherePart(final SafeAppendable sb, final boolean withTableAlis){
        final List<String> ws = new ArrayList<>();
        if(CollUtil.isNotEmpty(where)){
            ws.addAll(processFieldWithTableAlias(where, withTableAlis));
        }
        sqlClause(sb, "WHERE", ws, "(", ")", " AND ");
        sqlClause(sb, "GROUP BY", processFieldWithTableAlias(groupBy, withTableAlis), "", "", ", ");
        sqlClause(sb, "HAVING", processFieldWithTableAlias(having, withTableAlis), "(", ")", " AND ");
        sqlClause(sb, "ORDER BY", processFieldWithTableAlias(orderBy, withTableAlis), "", "", ", ");
    }

    protected void selectPart(final SafeAppendable builder, final boolean withTableAlis){
        final List<String> fs = new ArrayList<>();
        fs.addAll(selectFieldsWithTableAlias(withTableAlis));
        selectPart(builder, fs, withTableAlis);
    }

    protected void selectPart(final SafeAppendable builder, final List<String> fs, final boolean withTableAlis){
        if(CollUtil.isNotEmpty(fs)){
            if (distinct) {
                sqlClause(builder, "SELECT DISTINCT", fs, "", "", ", ");
            } else {
                sqlClause(builder, "SELECT", fs, "", "", ", ");
            }
        }else{
            sqlClause(builder, "SELECT", "*", "", "", ", ");
        }
    }


    public String selectSQL(){
        final SafeAppendable builder = new SafeAppendable();
        selectPart(builder, false);
        final String fromTableName = tableName;
        sqlClause(builder, "FROM", fromTableName, "", "", ", ");
        // where
        wherePart(builder, false);
        return builder.toString();
    }

    private String insertSQL() {
        final SafeAppendable builder = new SafeAppendable();
        sqlClause(builder, "INSERT INTO", tableName, "", "", "");
        sqlClause(builder, "", insertColumns, "(", ")", ", ");
        for (int i = 0; i < valuesList.size(); i++) {
            sqlClause(builder, i > 0 ? "," : "VALUES", valuesList.get(i), "(", ")", ", ");
        }
        return builder.toString();
    }

    private String deleteSQL() {
        final SafeAppendable builder = new SafeAppendable();
        sqlClause(builder, "DELETE FROM", tableName, "", "", "");
        sqlClause(builder, "WHERE", where, "(", ")", " AND ");
        return builder.toString();
    }

    private String updateSQL() {
        final SafeAppendable builder = new SafeAppendable();
        sqlClause(builder, "UPDATE", tableName, "", "", "");
        // joinSqlBlock(builder);
        sqlClause(builder, "SET", sets, "", "", ", ");
        sqlClause(builder, "WHERE", where, "(", ")", " AND ");
        return builder.toString();
    }

    /**
     * 是否作为子查询
     * @return
     */
    public boolean isAsSubQuery(){
        if(ObjectUtil.equal(true, distinct)
                || CollUtil.isNotEmpty(groupBy)
                || ObjectUtil.isNotNull(offset)
                || ObjectUtil.isNotNull(size)){
            return true;
        }
        return false;
    }

    public String subQuerySql(){
        return "( "+selectSQL()+" ) " + tableAlias;
    }

    /**
     * 数据表名称 带上 别名 table1 a
     * @return
     */
    protected String tableNameWithAlias(final boolean isWithTableAlias){
        return isWithTableAlias ? tableName+" "+tableAlias.alias() : tableName;
    }

    protected void sqlClause(SafeAppendable builder, String keyword, String part, String open, String close,
                             String conjunction) {
        sqlClause(builder, keyword, ListUtil.list(false, part), open, close, conjunction);
    }

    /**
     * sql片段
     * @param builder
     * @param keyword
     * @param parts
     * @param open
     * @param close
     * @param conjunction
     */
    protected void sqlClause(SafeAppendable builder, String keyword, List<String> parts, String open, String close,
                             String conjunction) {
        if(CollUtil.isEmpty(parts)){
            return;
        }
        if(keyword.equals("ON")){
            builder.append(" ");
        }else if (!builder.isEmpty()) {
            builder.append("\n");
        }
        builder.append(keyword);
        builder.append(" ");
        builder.append(open);
        String last = "________";
        for (int i = 0, n = parts.size(); i < n; i++) {
            String part = parts.get(i);
            if (i > 0 && !part.equals(SafeAppendable.AND) && !part.equals(SafeAppendable.OR) && !last.equals(SafeAppendable.AND) && !last.equals(SafeAppendable.OR)) {
                builder.append(conjunction);
            }
            builder.append(part);
            last = part;
        }
        builder.append(close);
    }

    @Override
    public String toString() {
        return sql();
    }

    public SqlTable clone(){
        final SqlTable sqlTable = new SqlTable();
        deepCopyTo(sqlTable);
        return sqlTable;
    }

    public void deepCopyTo(final SqlTable target){
        final SqlTable self = JSONUtil.toBean(JSONUtil.toJsonStr(this), JSONConfig.create()
                .setTransientSupport(true), SqlTable.class);
        BeanUtil.copyProperties(self, target);
        target.setTableAlias(tableAlias);
    }
}