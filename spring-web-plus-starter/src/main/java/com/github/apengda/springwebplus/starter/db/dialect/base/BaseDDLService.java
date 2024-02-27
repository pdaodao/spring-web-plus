package com.github.apengda.springwebplus.starter.db.dialect.base;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.IndexInfo;
import com.github.apengda.springwebplus.starter.db.dialect.DataTypeConverter;
import com.github.apengda.springwebplus.starter.db.dialect.DbDDLService;
import com.github.apengda.springwebplus.starter.db.pojo.ColumnInfo;
import com.github.apengda.springwebplus.starter.db.pojo.DDLBuildContext;
import com.github.apengda.springwebplus.starter.db.pojo.TableInfo;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BaseDDLService implements DbDDLService {
    protected final DataTypeConverter dataTypeConverter;

    public String quoteIdentifier(final String name){
        return name;
    }

    @Override
    public DataTypeConverter getDataTypeConverter() {
        return dataTypeConverter;
    }

    @Override
    public List<String> createTable(TableInfo tableInfo) {
        final String tableName = getFullTableName(tableInfo);
        final DDLBuildContext ddlBuildContext = DDLBuildContext.of(tableName, new ArrayList<>());

        final StringBuilder sql = new StringBuilder();
        sql.append(createTableSqlPrefix()).append(" ").append(tableName).append(" (\n");

        final int size = tableInfo.getColumns().size();
        int index = 0;
        // 普通字段
        for (final ColumnInfo field : tableInfo.getColumns()) {
            index++;
            // 生成字段定义
            final String fieldDdl = dataTypeConverter.fieldDDL(field, ddlBuildContext);
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
        if (CollUtil.isNotEmpty(tableInfo.getIndexInfoList())) {
            for (final IndexInfo indexInfo : tableInfo.getIndexInfoList()) {
                if ("PRIMARY".equalsIgnoreCase(indexInfo.getIndexName())) {
                    continue;
                }
                final String indexSql = genDDLOfCreateIndex(tableInfo.getTableName(), indexInfo);
                if (StrUtil.isNotBlank(indexSql)) {
                    ddlBuildContext.addSql(indexSql);
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
        final String name = tableInfo.getTableName().trim();
        if (StrUtil.isNotBlank(tableInfo.getSchema())) {
            return quoteIdentifier(tableInfo.getSchema()) + "."
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
        if(CollUtil.isEmpty(tableInfo.getPkNames())){
            return null;
        }
        final String names = tableInfo.getPkNames().stream()
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
     * @param tableName
     * @param indexInfo
     * @return
     */
    public String genDDLOfCreateIndex(final String tableName, final IndexInfo indexInfo){
        return null;
    }

    /**
     * 数据表注释语句
     *
     * @param tableInfo
     * @param ddlBuildContext
     * @return
     */
    protected  String genDDLTableComment(TableInfo tableInfo, final DDLBuildContext ddlBuildContext){
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
}
