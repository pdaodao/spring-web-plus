package com.github.pdaodao.springwebplus.starter.db.util;

import cn.hutool.core.map.CaseInsensitiveLinkedMap;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.starter.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.starter.db.dialect.DbFactory;
import com.github.pdaodao.springwebplus.starter.db.pojo.ColumnInfo;
import com.github.pdaodao.springwebplus.starter.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.starter.db.pojo.TableInfo;
import lombok.extern.slf4j.Slf4j;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DDLUtil {

    /**
     * 扫描实体结构信息和数据库表结构信息进行对比
     *
     * @param ds
     * @param isDeleteField
     */
    public static void dbCheck(final DataSource ds, final boolean isDeleteField) {
        final List<TableInfo> entityList = DbEntityUtil.entityList();
        final List<TableInfo> tables = DbMetaUtil.tableInfoList(ds);
        final Map<String, TableInfo> oldMap = new HashMap<>();
        tables.forEach(t -> oldMap.put(t.getTableName(), t));

        final DbDialect dbDialect = DbFactory.of(ds);

        for (final TableInfo info : entityList) {
            tableCheck(dbDialect, isDeleteField, ds, info, oldMap.get(info.getTableName()));
        }
    }

    /**
     * 检查数据表结构变化
     * @param dbDialect
     * @param isDeleteField
     * @param ds
     * @param tableInfo
     * @param old
     */
    public static void tableCheck(final DbDialect dbDialect,
                                  final boolean isDeleteField,
                                  final DataSource ds, final TableInfo tableInfo, final TableInfo old) {
        final List<String> sqls = genDDL(dbDialect, isDeleteField, tableInfo, old);
        log.info("------------- " + tableInfo.getTableName() + "---------");
        log.info(StrUtil.join(";\n", sqls));
        try {
            DbUtil.executeSqlBlock(ds, sqls);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 对比表结构生成建表或者更新表结构语句
     * @param dbDialect
     * @param tableInfo
     * @param old
     * @return
     */
    public static List<String> genDDL(final DbDialect dbDialect,
                                      final boolean isDeleteField,
                                      final TableInfo tableInfo, final TableInfo old) {
        // 新建表
        if (old == null) {
            return dbDialect.ddlGen().createTable(tableInfo);
        }
        // 对比上次表结构同步后的变化
        final CaseInsensitiveLinkedMap<String, ColumnInfo> fieldMap = new CaseInsensitiveLinkedMap();
        final CaseInsensitiveLinkedMap<String, ColumnInfo> oldFieldMap = new CaseInsensitiveLinkedMap();

        final DDLBuildContext ddlBuildContext = new DDLBuildContext(tableInfo.getTableName());
        for (final ColumnInfo f : tableInfo.getColumns()) {
            fieldMap.put(f.getName(), f);
        }
        for (final ColumnInfo f : old.getColumns()) {
            oldFieldMap.put(f.getName(), f);
            // 删除旧的已经不用的字段
            if (!fieldMap.containsKey(f.getName())) {
                if(true == isDeleteField){
                    final List<String> dSqls = dbDialect.ddlGen().dropColumnSql(tableInfo.getTableName(), f.getName());
                    ddlBuildContext.addSql(dSqls);
                }
                continue;
            }
            // 字段结构发生变化 更新
            final ColumnInfo toUpdated = fieldMap.get(f.getName());
            if (toUpdated.diff(f, false)) {
                final List<String> alterSqls = dbDialect.ddlGen().alterColumnSql(f, toUpdated, ddlBuildContext);
                ddlBuildContext.addSql(alterSqls);
            }
        }
        // 新增字段
        for (final ColumnInfo f : tableInfo.getColumns()) {
            if (!oldFieldMap.containsKey(f.getName())) {
                ddlBuildContext.addSql(dbDialect.ddlGen().addColumnSql(f, ddlBuildContext));
            }
        }
        ddlBuildContext.addSql(ddlBuildContext.lastSql);
        return ddlBuildContext.sqls;
    }

}
