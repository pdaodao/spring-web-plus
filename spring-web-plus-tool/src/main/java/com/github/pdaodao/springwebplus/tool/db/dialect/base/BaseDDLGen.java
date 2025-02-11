package com.github.pdaodao.springwebplus.tool.db.dialect.base;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.core.TableIndex;
import com.github.pdaodao.springwebplus.tool.db.core.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDDLGen;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BaseDDLGen implements DbDDLGen {
    protected final DbDialect dbDialect;


    public String quoteIdentifier(final String name) {
        return dbDialect.quoteIdentifier(name);
    }

    @Override
    public List<String> createTable(TableInfo tableInfo) {
        final String tableName = getFullTableName(tableInfo);
        final DDLBuildContext ddlBuildContext = DDLBuildContext.of(tableName);

        final StringBuilder sql = new StringBuilder();
        sql.append(createTableSqlPrefix()).append(" ").append(tableName).append(" (\n");

        final int size = tableInfo.getColumns().size();
        int index = 0;
        // 普通字段
        for (final TableColumn field : tableInfo.getColumns()) {
            index++;
            // 生成字段定义
            final String fieldDdl = dbDialect.dataTypeConverter().fieldDDL(null, field, ddlBuildContext);
            sql.append(fieldDdl);
            if (index < size) {
                sql.append(",");
                sql.append("\n");
            }
        }
        // 主键字段
        final String pk = genDDLOfPk(tableInfo, ddlBuildContext);
        if (StrUtil.isNotBlank(pk)) {
            sql.append(",\n").append(pk);
        }
        // 索引
        if (CollUtil.isNotEmpty(tableInfo.getIndexList())) {
            for (final TableIndex indexInfo : tableInfo.getIndexList()) {
                if ("PRIMARY".equalsIgnoreCase(indexInfo.getName()) || indexInfo.getName().endsWith("_pkey")) {
                    continue;
                }
                final String indexSql = genDDLOfCreateIndex(tableInfo, indexInfo);
                if (StrUtil.isNotBlank(indexSql)) {
                    ddlBuildContext.addLastSql(indexSql);
                }
            }
        }
        // 字段描述部分结束 右括号
        sql.append(") ");

        // 有些数据库如 Doris 主键信息写在后面的表信息中
        final String pkTable = genDDLOfPkTable(tableInfo, ddlBuildContext);
        if (StrUtil.isNotBlank(pkTable)) {
            sql.append(pkTable).append(" ");
        }
        // 分区
        final String pt = genDDLOfPartition(tableInfo, ddlBuildContext);
        if (StrUtil.isNotBlank(pt)) {
            sql.append(" ").append(pt);
        }
        // 数据表注释
        final String comment = genDDLTableComment(tableInfo, ddlBuildContext);
        if (StrUtil.isNotEmpty(comment)) {
            sql.append(" ").append(comment);
        }
        // 数据表格式 引擎信息
        final String engine = genDDLTableEngineInfo(tableInfo, ddlBuildContext);
        if (StrUtil.isNotBlank(engine)) {
            sql.append(" ").append(engine);
        }
        final List<String> result = new ArrayList<>();
        result.add(sql.toString());
        result.addAll(ddlBuildContext.lastSql);
        return result;
    }

    /**
     * 表名全路径
     *
     * @param tableInfo
     * @return
     */
    public String getFullTableName(final TableInfo tableInfo) {
        final String name = tableInfo.getName().trim();
        if (StrUtil.isNotBlank(tableInfo.getDbSchema())) {
            return quoteIdentifier(tableInfo.getDbSchema()) + "."
                    + quoteIdentifier(name);
        }
        return quoteIdentifier(name);
    }

    protected String createTableSqlPrefix() {
        return "CREATE TABLE ";
    }


    /**
     * 字段主键语句部分
     *
     * @param tableInfo
     * @param context
     * @return
     */
    public String genDDLOfPk(TableInfo tableInfo, final DDLBuildContext context) {
        if (CollUtil.isEmpty(tableInfo.pkColumns())) {
            return null;
        }
        final String names = tableInfo.pkColumns().stream()
                .map(t -> quoteIdentifier(t))
                .collect(Collectors.joining(","));
        if (StrUtil.isEmpty(names)) {
            return null;
        }
        return "PRIMARY KEY (" + names + ")";
    }

    /**
     * 主键信息数据表描述部分
     *
     * @param tableInfo
     * @param context
     * @return
     */
    public String genDDLOfPkTable(TableInfo tableInfo, DDLBuildContext context) {
        return null;
    }

    /**
     * 按字段分区
     *
     * @param tableInfo
     * @param context
     * @return
     */
    protected String genDDLOfPartition(TableInfo tableInfo, DDLBuildContext context) {
        return null;
    }

    /**
     * 创建索引
     *
     * @param tableInfo
     * @param indexInfo
     * @return
     */
    public String genDDLOfCreateIndex(final TableInfo tableInfo, final TableIndex indexInfo) {
        return null;
    }

    /**
     * 数据表注释语句
     *
     * @param tableInfo
     * @param ddlBuildContext
     * @return
     */
    protected String genDDLTableComment(TableInfo tableInfo, final DDLBuildContext ddlBuildContext) {
        return null;
    }

    /**
     * 最后的数据表格式 引擎信息
     *
     * @param tableInfo
     * @param context
     * @return
     */
    protected String genDDLTableEngineInfo(TableInfo tableInfo, DDLBuildContext context) {
        return null;
    }

    protected String indexInfoColumns(final TableIndex tableIndex) {
        return tableIndex.getFields().stream().map(t -> quoteIdentifier(t))
                .collect(Collectors.joining(","));
    }


    @Override
    public List<String> addColumnSql(final TableColumn tableColumn, final DDLBuildContext ddlBuildContext) {
        final List<String> last = new ArrayList<>();
        final List<String> list = new ArrayList<>();
        final String sql = String.format(
                "ALTER TABLE %s ADD COLUMN %s", quoteIdentifier(ddlBuildContext.tableName),
                dbDialect.dataTypeConverter().fieldDDL(null, tableColumn, ddlBuildContext));
        list.add(sql);
        list.addAll(last);
        return list;
    }


    @Override
    public List<String> renameColumnSql(final String tableName, String fromColumnName, String toColumnName) {
        final List<String> list = new ArrayList<>();
        final String sql = String.format("ALTER TABLE %s RENAME COLUMN %s TO %s",
                quoteIdentifier(tableName),
                quoteIdentifier(fromColumnName),
                quoteIdentifier(toColumnName));
        list.add(sql);
        return list;
    }

    @Override
    public List<String> dropColumnSql(final String tableName, String columnName) {
        final List<String> list = new ArrayList<>();
        final String sql = String.format("ALTER TABLE %s DROP COLUMN %s",
                quoteIdentifier(tableName), quoteIdentifier(columnName));
        list.add(sql);
        return list;
    }

    @Override
    public List<String> alterColumnSql(final TableColumn from, final TableColumn to, final DDLBuildContext ddlBuildContext) {
        Preconditions.checkNotNull(from.getDataType(), "{} alterColumnSql from dataType is null of table:{}", from.getName(), ddlBuildContext.tableName);
        Preconditions.checkNotNull(to.getDataType(), "{} alterColumnSql to dataType is null of table:{}", to.getName(), ddlBuildContext.tableName);
        final List<String> list = new ArrayList<>();
        if (!StrUtil.equals(from.getName(), to.getName())) {
            // 字段名称修改
            list.addAll(renameColumnSql(ddlBuildContext.tableName, from.getName(), to.getName()));
        }
        if (from.diff(to, true) && !(BooleanUtil.isTrue(to.isAuto) && from != null && BooleanUtil.isTrue(from.isAuto))) {
            ddlBuildContext.isModifyColumn = true;
            final List<String> last = new ArrayList<>();
            final String sql = String.format(
                    "ALTER TABLE %s %s %s",
                    quoteIdentifier(ddlBuildContext.tableName),
                    modifyColumn(),
                    dbDialect.dataTypeConverter().fieldDDL(from, to, ddlBuildContext));
            ddlBuildContext.isModifyColumn = false;
            list.add(sql);
            list.addAll(last);
        }
        return list;
    }

    protected String modifyColumn() {
        return "MODIFY COLUMN";
    }
}
